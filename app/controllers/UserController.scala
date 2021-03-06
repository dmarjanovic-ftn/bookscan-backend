package controllers

import java.util.Date
import javax.inject.Inject

import authentication.{Authentication, LoginResponse, TokenRequest}
import authentikat.jwt.{JsonWebToken, JwtClaimsSet, JwtHeader}
import com.google.inject.Singleton
import dao.UserDAO
import models.User
import models.dto.LoginDTO
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, Controller}

import concurrent.{Promise, _}
import play.api.libs.json._

import ExecutionContext.Implicits.global


@Singleton
class UserController @Inject()(usersDao: UserDAO, val messagesApi: MessagesApi, val auth: Authentication) extends Controller with I18nSupport {

  def getUser(id: Long) = Action.async { implicit rs =>
    val data = for {
      user <- usersDao.findById(id)
    } yield user

    data.map { case (user) =>
      user match {
        case Some(u) =>
          Ok(Json.toJson(u))
            .withHeaders(("Content-type", "application/json; charset=utf-8"))
        case None => NotFound
      }
    }
  }

  def getLoggedUser() = auth.HasToken {
    Action.async(parse.empty) {
      case TokenRequest(email, rs) =>
        val data = for {
          user <- usersDao.findOneByEmail(email)
        } yield user

        data.map { case ( u ) => Ok(Json.toJson(u)) }
    }
  }

  def register() = Action(parse.json) { request =>
    request.body.validate[User].map {
      user => usersDao.insert(user)
        Ok("User successfully saved!")
    }.recoverTotal {
        e => BadRequest(Json.obj("status" -> "KO", "message" -> JsError.toJson(e)))
    }
  }

  def authenticate = Action.async(parse.json[LoginDTO]) { implicit rs =>
    val userBody = rs.body

    val data = for {
      user <- usersDao.findOneByEmailAndPassword(userBody.email, userBody.password)
    } yield user

    data.map {
      case Some(u) => {
        u match {
          case us: User => {
            val header = JwtHeader("HS256")
            val claimsSet = JwtClaimsSet(Map("email" -> us.email))
            val jwt: String = JsonWebToken(header, claimsSet, (new Date()).toString())

            Ok(Json.toJson(LoginResponse(jwt)))
              .withHeaders(("Content-type", "application/json; charset=utf-8"))
          }
          case _ => Unauthorized("User with wrong credentials")
        }
      }
      case _ => Unauthorized("User with wrong credentials")
    }
  }

  def userExists(email: String) = Action.async { implicit rs =>
      val data = for {
        user <- usersDao.exists(email)
      } yield user

      data.map { case ( exists ) => Ok(Json.obj("exists" -> exists)) }
  }
}