package code
package model

import lib.RogueMetaRecord
import omniauth.AuthInfo

import org.bson.types.ObjectId
import org.joda.time.DateTime

import net.liftweb._
import common._
import http.{StringField => _, BooleanField => _, _}
import mongodb.record.field._
import record.field.{PasswordField => _, _}
import net.liftweb.util.{FieldError, Helpers, FieldContainer}

import net.liftmodules.mongoauth._
import net.liftmodules.mongoauth.field._
import net.liftmodules.mongoauth.model._

import scala.xml.Text

class User private () extends MongoAuthUser[User] with ObjectIdPk[User] {
  def meta = User

  def userIdAsString: String = id.toString

  import Helpers._

  object username extends StringField(this, 32) {
    override def displayName = "Nombre de usuario"
    override def setFilter = trim _ :: super.setFilter

    private def valUnique(msg: => String)(value: String): List[FieldError] = {
      if (value.length > 0)
        meta.findAll(name, value).filterNot(_.id.get == owner.id.get).map(u =>
          FieldError(this, Text(msg))
        )
      else
        Nil
    }

    override def validations =
      valUnique(S ? "El nombre de usuario ya esta siendo usado") _ ::
        valMinLen(3, S ? "El nombre de usuario debe tener al menos 3 caracteres") _ ::
        valMaxLen(32, S ? "El nombre de usuario debe tener a lo sum 32 caracteres") _ ::
        super.validations
  }

  /*
  * http://www.dominicsayers.com/isemail/
  */
  object email extends EmailField(this, 254) {
    override def displayName = "Correo electrónico"
    override def setFilter = trim _ :: toLower _ :: super.setFilter

    private def valUnique(msg: => String)(value: String): List[FieldError] = {
      owner.meta.findAll(name, value).filter(_.id.get != owner.id.get).map(u =>
        FieldError(this, Text(msg))
      )
    }

    override def validations =
      valUnique("Esta dirección de correo electrónico ya esta registrada") _  ::
        valMaxLen(254, "La dirección de correo debe tener menos de 254 caracteres") _ ::
        super.validations
  }

  // email address has been verified by clicking on a LoginToken link
  object verified extends BooleanField(this) {
    override def displayName = "Verificada"
  }

  object password extends PasswordField(this, 6, 32) {
    override def displayName = "Contraseña"
  }

  object permissions extends PermissionListField(this)
  object roles extends StringRefListField(this, Role) {
    def permissions: List[Permission] = objs.flatMap(_.permissions.get)
    def names: List[String] = objs.map(_.id.get)
  }

  lazy val authPermissions: Set[Permission] = (permissions.get ::: roles.permissions).toSet
  lazy val authRoles: Set[String] = roles.names.toSet

  lazy val fancyEmail = AuthUtil.fancyEmail(username.get, email.get)

  object locale extends LocaleField(this) {
    override def displayName = "Idioma"
    override def defaultValue = "en_US"
  }
  object timezone extends TimeZoneField(this) {
    override def displayName = "Zona horaria"
    override def defaultValue = "America/Chicago"
  }

  object name extends StringField(this, 64) {
    override def displayName = "Nombre"

    override def validations =
      valMaxLen(64, "El nombre debe tener menos de 64 caracteres") _ ::
      super.validations
  }
  object location extends StringField(this, 64) {
    override def displayName = "Ubicación"

    override def validations =
      valMaxLen(64, "La ubicación debe tener menos de 64 caracteres") _ ::
      super.validations
  }
  object bio extends TextareaField(this, 160) {
    override def displayName = "Bio"

    override def validations =
      valMaxLen(160, "Bio debe tener 100 caracteres o menos") _ ::
      super.validations
  }

  object snUsername extends OptionalStringField(this, 64) {
    override def displayName = "Sn Username"

    override def shouldDisplay_? = false
  }

  /*
   * FieldContainers for various LiftScreeens.
   */
  def accountScreenFields = new FieldContainer {
    def allFields = List(username, email, locale, timezone)
  }

  def profileScreenFields = new FieldContainer {
    def allFields = List(name, location, bio)
  }

  def registerScreenFields = new FieldContainer {
    def allFields = List(username, email)
  }

  def whenCreated: DateTime = new DateTime(id.get.getDate)
}

object User extends User with ProtoAuthUserMeta[User] with RogueMetaRecord[User] with Loggable {
  import mongodb.BsonDSL._

  override def collectionName = "user.users"

  createIndex((email.name -> 1), true)
  createIndex((username.name -> 1), true)

  def findByEmail(in: String): Box[User] = find(email.name, in)
  def findByUsername(in: String): Box[User] = find(username.name, in)
  def findBySnUsername(in: String): Box[User] = find(snUsername.name, in)

  def findByStringId(id: String): Box[User] =
    if (ObjectId.isValid(id)) find(new ObjectId(id))
    else Empty

  override def onLogIn: List[User => Unit] = List(user => User.loginCredentials.remove())
  override def onLogOut: List[Box[User] => Unit] = List(
    x => logger.debug("User.onLogOut called."),
    boxedUser => boxedUser.foreach { u =>
      ExtSession.deleteExtCookie()
    }
  )

  /*
   * MongoAuth vars
   */
  private lazy val siteName = MongoAuth.siteName.vend
  private lazy val sysUsername = MongoAuth.systemUsername.vend
  private lazy val indexUrl = MongoAuth.indexUrl.vend
  private lazy val registerUrl = MongoAuth.registerUrl.vend
  private lazy val loginTokenAfterUrl = MongoAuth.loginTokenAfterUrl.vend

  /*
   * LoginToken
   */
  override def handleLoginToken: Box[LiftResponse] = {
    val resp = S.param("token").flatMap(LoginToken.findByStringId) match {
      case Full(at) if (at.expires.isExpired) => {
        at.delete_!
        RedirectWithState(indexUrl, RedirectState(() => { S.error("Login token ha expirado") }))
      }
      case Full(at) => find(at.userId.get).map(user => {
        if (user.validate.length == 0) {
          user.verified(true)
          user.update
          logUserIn(user)
          at.delete_!
          RedirectResponse(loginTokenAfterUrl)
        }
        else {
          at.delete_!
          regUser(user)
          RedirectWithState(registerUrl, RedirectState(() => { S.notice("Por favor termine el formulario de registro") }))
        }
      }).openOr(RedirectWithState(indexUrl, RedirectState(() => { S.error("Usuario no encontrado") })))
      case _ => RedirectWithState(indexUrl, RedirectState(() => { S.warning("Login token no provisto") }))
    }

    Full(resp)
  }

  // send an email to the user with a link for logging in
  def sendLoginToken(user: User): Unit = {
    import net.liftweb.util.Mailer._

    LoginToken.createForUserIdBox(user.id.get).foreach { token =>

      val msgTxt =
        """
          |Alguien ha solicitado un enlace para cambiar tu contraseña en el sitio web %s .
          |
          |Si tu no solicitaste esto, puedes ignorar este mensaje. Expirará 48 horas despues de que este mensaje ha sido enviado.
          |
          |Haz click en el siguiente enlace o copia y pega en tu navegador.
          |
          |%s
          |
          |Gracias,
          |%s
        """.format(siteName, token.url, sysUsername).stripMargin

      sendMail(
        From(MongoAuth.systemFancyEmail),
        Subject("%s Ayuda contraseña".format(siteName)),
        To(user.fancyEmail),
        PlainMailBodyType(msgTxt)
      )
    }
  }

  /*
   * ExtSession
   */
  def createExtSession(uid: ObjectId): Box[Unit] = ExtSession.createExtSessionBox(uid)

  /*
  * Test for active ExtSession.
  */
  def testForExtSession: Box[Req] => Unit = {
    ignoredReq => {
      if (currentUserId.isEmpty) {
        ExtSession.handleExtSession match {
          case Full(es) => find(es.userId.get).foreach { user => logUserIn(user, false) }
          case Failure(msg, _, _) =>
            logger.warn("Error logging user in with ExtSession: %s".format(msg))
          case Empty =>
        }
      }
    }
  }

  // used during login process
  object loginCredentials extends SessionVar[LoginCredentials](LoginCredentials(""))
  object regUser extends SessionVar[User](createRecord.email(loginCredentials.is.email))


  //Omnitauth login
  def loginWithOmniauth(authInfo: AuthInfo): Box[User] = {
    val name = authInfo.name
    println(authInfo)
    authInfo.email.flatMap(User.findByEmail(_)) match {
      case None if authInfo.provider != "twitter" =>
        val user = User
          .createRecord
          .name(name)
          .username(s"${authInfo.provider}_${authInfo.nickName getOrElse authInfo.uid}")
          .verified(true)
          .email(authInfo.email getOrElse s"${authInfo.provider}_${authInfo.nickName getOrElse authInfo.uid}")
          .password(Helpers.nextFuncName)
          .save(true)
        logUserIn(user, true)
        Full(user)
      case None =>
        User.findBySnUsername(s"${authInfo.provider}_${authInfo.nickName getOrElse authInfo.uid}") match {
          case x@Full(user) =>
            logUserIn(user, true)
            x
          case _ =>
            val user = User
              .createRecord
              .name(name)
              .snUsername(s"${authInfo.provider}_${authInfo.nickName getOrElse authInfo.uid}")
              .username(s"${authInfo.provider}_${authInfo.nickName getOrElse authInfo.uid}")
              .verified(true)
              .email(authInfo.email getOrElse s"${authInfo.provider}_${authInfo.nickName getOrElse authInfo.uid}")
              .password(Helpers.nextFuncName)
              .save(true)
            logUserIn(user, true)
            Full(user)
        }
      case Some(user) =>
        logUserIn(user)
        Full(user)
    }

  }
}

case class LoginCredentials(email: String, isRememberMe: Boolean = false)

