import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.Materializer
import config.ServerConfig
import controller.{UrlShrinkController, UrlShrinkRoutes}

object Main extends App {
  private val config = ServerConfig.load()

  implicit val system: ActorSystem = ActorSystem("url-shrinker")
  implicit val materializer: Materializer = Materializer(system)
  implicit val executionContext = system.dispatcher

  val urlShrinkController = new UrlShrinkController()
  val urlShrinkRoutes = new UrlShrinkRoutes(urlShrinkController)

  val bindingFuture = Http().newServerAt(config.host, config.port).bind(urlShrinkRoutes.routes)

  println("Server is running")

  sys.addShutdownHook {
    bindingFuture.flatMap(_.unbind())
    system.terminate()
  }
}
