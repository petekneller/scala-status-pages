package com.github.petekneller.statuspages

import com.github.petekneller.statuspages.Rendering._
import io.shaka.http.ContentType
import io.shaka.http.ContentType._
import io.shaka.http.Request
import io.shaka.http.RequestMatching.RequestOps
import io.shaka.http.Response
import io.shaka.http.Response.ok
import io.shaka.http.Status.SERVICE_UNAVAILABLE

object NaiveHttp {
  private def hasRequested(req: Request, contentType: ContentType): Boolean = req.accepts(contentType)

  def negotiateContentType(req: Request, status: Seq[Status]): Response = {
    // Content neg works as follows:
    // - if you ask for json explicitly, you get json
    // - if you ask for text/html explicitly, you get html (most browsers should specify html in the accepted types)
    // - otherwise you get text/plain
    val isJson = req.accepts(APPLICATION_JSON)
    val isHtml = req.accepts(TEXT_HTML)
    val formatter = if (isJson) toJson _ else if (isHtml) toHtml(_: Seq[Status], false) else toText _
    val contentType = if (isJson) APPLICATION_JSON else if (isHtml) TEXT_HTML else TEXT_PLAIN
    formatter(status).fold(err => Response(SERVICE_UNAVAILABLE).entity(err), ok.entity(_)).contentType(contentType)
  }

}
