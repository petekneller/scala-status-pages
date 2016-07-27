package com.github.petekneller.statuspages

import com.github.petekneller.statuspages.Rendering._
import net.liftweb.common.{Box, Full}
import net.liftweb.http.{HeaderDefaults, InMemoryResponse, LiftResponse, Req}

object Liftweb {

  private def response(body: String, status: Int, contentType: String): LiftResponse = new LiftResponse with HeaderDefaults {
    override def toResponse = InMemoryResponse(body.getBytes("utf-8"), headers :+ ("Content-Type" -> contentType), cookies, status)
  }

  def negotiateContentType(req: Req, status: Seq[Status]): Box[LiftResponse] = {
    // Content neg works as follows:
    // - if you ask for json explicitly, you get json
    // - if you ask for text/html explicitly, you get html (most browsers should specify html in the accepted types)
    // - otherwise you get text/plain
    val isJson = req.accepts.map(_ contains "application/json").getOrElse(false)
    val isHtml = req.accepts.map(_ contains "text/html").getOrElse(false)
    val formatter = if (isJson) toJson _ else if (isHtml) toHtml(_: Seq[Status], false) else toText _
    val contentType = if (isJson) "application/json" else if (isHtml) "text/html" else "text/plain"

    Full(formatter(status).fold(response(_, 503, contentType), response(_, 200, contentType)))
  }

}
