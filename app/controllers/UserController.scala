package controllers

import javax.inject.Inject

import com.google.inject.Singleton
import dao.UserDAO
import models.User
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.{JsError, Json}
import play.api.mvc.{Action, Controller}
import play.api.libs.concurrent.Execution.Implicits.defaultContext

@Singleton
class UserController @Inject()(usersDao: UserDAO, val messagesApi: MessagesApi) extends Controller with I18nSupport {

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


  def register() = Action(parse.json) { request =>
    request.body.validate[User].map {
      user => usersDao.insert(user)
        Ok("User succesfully saved!")
    }.recoverTotal {
        e => BadRequest(Json.obj("status" -> "KO", "message" -> JsError.toJson(e)))
    }
  }


  def userExists(email: String) = Action.async { implicit rs =>
    val data = for {
      user <- usersDao.exists(email)
    } yield user

    data.map { case (exists) => Ok(Json.obj("exists" -> exists)) }
  }


}