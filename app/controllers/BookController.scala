package controllers

import javax.inject.{Inject, Singleton}

import dao.{UserDAO}
import models.{Book, User}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

import scalaj.http.Http

@Singleton
class BookController @Inject() (usersDao: UserDAO, val messagesApi: MessagesApi) extends Controller with I18nSupport {

  def index() = Action {
    Ok(views.html.index("Welcome to books' search engine... "))
  }

  def getBook(isbn: String) = Action {

    // 978-86-88003-71-1
    // 978-86-6105-041-1
    // ID dobij ljepse
    val response = Http("http://www.vbs.rs/scripts/cobiss?ukaz=GETID").asString.body
    val start = response.indexOf("http://www.vbs.rs/scripts/cobiss?ukaz=SFRM&amp;id=") +
      "http://www.vbs.rs/scripts/cobiss?ukaz=SFRM&amp;id=".length
    val id = response.substring(start, start + 16)


    // Parametre ljepse
    val params = Seq("ukaz" -> "SEAR", "ID" -> id, "keysbm" -> "srcfrm", "PF1" -> "BN", "SS1" -> isbn,
      "OP1" -> "AND",  "PF2" -> "TI", "SS2" -> "", "OP2" -> "AND", "PF3" -> "PY", "SS3" -> "", "OP3" -> "AND",
      "PF4" -> "KW", "SS4" -> "", "lan" -> "", "mat" -> "51", "scpt" -> "")

    // Html Parse to Book JSON
    val html = Http("http://www.vbs.rs/scripts/cobiss?id=" + id).postForm(params).asString.body
    val book = Book.parseHtml(html)

    Ok(Json.toJson(book))
      .withHeaders(("Content-type", "application/json; charset=utf-8"))
  }

}
