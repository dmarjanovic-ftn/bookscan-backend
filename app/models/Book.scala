package models

import play.api.libs.json.{JsValue, Json, Writes}

case class Book(id: Option[Long] = None, title: String, author: String, language: String, year: String, publisher: String) {

}

object Book {

  def parseHtml(html: String): Book = {

    val keys = Seq("Наслов", "Аутор", "Језик", "Година", "Издавање и производња")
    val values = keys map (key => extract(key, html))

    // FIXME Figure out better way to make class with Seq's parameters
    new Book(None, values(0), values(1), values(2), values(3), values(4))
  }

  implicit val implicitBookWrites = new Writes[Book] {
    def writes(book: Book): JsValue = {
      Json.obj(
        "title"     -> book.title,
        "author"    -> book.author,
        "language"  -> book.language,
        "year"      -> book.year,
        "publisher" -> book.publisher
      )
    }
  }

  // FIXME with better implementation
  private def extract(key: String, html: String): String = {

    var beg = html.indexOf(key) + key.length + 9
    beg = if (html.charAt(beg) == '<') html.indexOf(">", beg) + 1 else beg

    val end = html.indexOf("<", beg+1)

    html.substring(beg, end).trim
  }
}