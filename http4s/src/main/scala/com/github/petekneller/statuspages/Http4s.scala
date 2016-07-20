package com.github.petekneller.statuspages.http4s

import com.github.petekneller.statuspages.Rendering._
import com.github.petekneller.statuspages.Status
import org.http4s.dsl._
import org.http4s.headers.Accept
import org.http4s.{MediaType, Request, Response}

import scalaz.concurrent.Task

object Http4s {
  private def hasRequested(req: Request, mediaType: MediaType): Boolean = (for {
    accept <- req.headers.get(Accept)
  } yield accept.values.head == mediaType).getOrElse(false)

  def negotiateContentType(req: Request, status: Seq[Status]): Task[Response] = {
    // Content neg works as follows:
    // - if you ask for json explicitly, you get json
    // - if you ask for text/html explicitly, you get html (most browsers should specify html in the accepted types)
    // - otherwise you get text/plain
    val isJson = hasRequested(req, MediaType.`application/json`)
    val isHtml = hasRequested(req, MediaType.`text/html`)
    val formatter = if (isJson) toJson _ else if (isHtml) toHtml _ else toText _
    val contentType = if (isJson) MediaType.`application/json` else if (isHtml) MediaType.`text/html` else MediaType.`text/plain`
    formatter(status).fold(ServiceUnavailable(_), Ok(_)).withContentType(Some(contentType))
  }
}
