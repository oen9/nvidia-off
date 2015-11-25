import akka.actor.{ActorSystem, Props}
import com.typesafe.scalalogging.StrictLogging

import scala.concurrent.duration._

object Main extends App with StrictLogging {
  logger.info("App started")

  val system = ActorSystem("nvidia-off")

  val nvidiaScourerActor = system.actorOf(Props(classOf[NvidiaScourer]))
  //Use system's dispatcher as ExecutionContext
  import system.dispatcher

  val interval = if (0 == args.length) 30 else args(0).toInt
  val cancellable = system.scheduler.schedule(
    interval second,
    interval second,
    nvidiaScourerActor,
    NvidiaScourer.Tick)

  Runtime.getRuntime.addShutdownHook(new Thread() {
    override def run(): Unit = close
  })

  def close: Unit = {
    cancellable.cancel()
    system.terminate()
    logger.info("App closed")
  }
}
