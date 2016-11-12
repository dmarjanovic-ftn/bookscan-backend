package authentication

import play.api.libs.json.{JsValue, Json, Reads, Writes, _}

case class LoginResponse(token: String)

object LoginResponse{
  implicit val implicitLoginWrites = new Writes[LoginResponse] {
    def writes(login: LoginResponse): JsValue = {
      Json.obj(
        "token" -> login.token
      )
    }
  }

  implicit val implicitLoginReads: Reads[LoginResponse] =
    (__ \ "token").read[String].map { token => LoginResponse(token) }
}
