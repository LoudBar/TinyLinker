package domain

case class OriginalUrl(value: String)
case class ShrunkUrl(value: String)
final case class Url(originalUrl: OriginalUrl, shortenedUrl: ShrunkUrl)
