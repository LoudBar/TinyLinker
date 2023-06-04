package service

import dao.RedisDb
import domain._
import java.security.MessageDigest
import scala.concurrent.{ExecutionContext, Future}

object UrlShrinkService {
  private val base62Chars: String = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
  private val base62Length: Int = base62Chars.length
  private val stubUrl = ""

  def retrieveOriginalUrl(shortUrl: String)(implicit ec: ExecutionContext): Future[Option[String]] = {
    val url = Url(OriginalUrl(stubUrl), ShrunkUrl(shortUrl))
    RedisDb.retrieveURL(url)
  }

  def shrinkUrl(originalUrl: String)(implicit ec: ExecutionContext): Future[String] = {
    val key = generateUniqueKey(originalUrl)
    val shortenedUrl = s"http://localhost:8080/${base62Encode(key)}"
    val url = Url(OriginalUrl(originalUrl), ShrunkUrl(shortenedUrl))
    RedisDb.storeURL(url).map(_ => shortenedUrl)
  }

  private def generateUniqueKey(originalUrl: String): Long = {
    val md = MessageDigest.getInstance("SHA-256")
    val digest = md.digest(originalUrl.getBytes("UTF-8"))
    val hash = BigInt(1, digest).toLong
    hash.abs
  }

  private def base62Encode(number: Long): String = {
    @scala.annotation.tailrec
    def encodeHelper(number: Long, acc: StringBuilder): String = {
      if (number <= 0) acc.reverse.toString()
      else {
        val index = (number % base62Length).toInt
        val nextAcc = acc.append(base62Chars.charAt(index))
        encodeHelper(number / base62Length, nextAcc)
      }
    }
    encodeHelper(number, new StringBuilder())
  }
}
