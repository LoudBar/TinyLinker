package dao

import akka.actor.ActorSystem
import akka.event.Logging
import config.DatabaseConfig
import domain.Url
import redis.clients.jedis.{Jedis, JedisPool, JedisPoolConfig}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Try, Using}

object RedisDb {
  private object RedisConnector {
    private val config = DatabaseConfig.load()
    private val jedisPool = new JedisPool(new JedisPoolConfig(), config.host, config.port)

    def withJedis[A](body: Jedis => A): Try[A] = {
      Using(jedisPool.getResource) { jedis =>
        body(jedis)
      }
    }
  }

  def storeURL(url: Url)(implicit ec: ExecutionContext): Future[Unit] = {
    Future.fromTry(RedisConnector.withJedis { jedis =>
      jedis.set(url.shortenedUrl.value, url.originalUrl.value)
    }).map(_ => ())
  }

  def retrieveURL(url: Url)(implicit ec: ExecutionContext): Future[Option[String]] = {
    Future.fromTry(RedisConnector.withJedis { jedis =>
      Option(jedis.get(url.shortenedUrl.value))
    })
  }

}


