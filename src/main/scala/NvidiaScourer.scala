import java.io.File

import akka.actor.Actor
import com.typesafe.scalalogging.LazyLogging

import scala.io.Source
import scala.sys.process.{ProcessLogger, _}

class NvidiaScourer extends Actor with LazyLogging {

  val bbswitchPath = "/proc/acpi/bbswitch"
  val bumblebeeUniqueKey = "bumblebee/xorg.conf.nvidia"
  val emptyProcessLogger = ProcessLogger(fout => (), ferr => ())

  override def receive = {
    case NvidiaScourer.Tick => clean()
  }

  def clean(): Unit = {
    val result = "ps ax" #| s"grep $bumblebeeUniqueKey" #| "grep -v grep" ! emptyProcessLogger

    if (0 != result) {
      rmmod
      turnOffBbswitch
    }
  }

  def rmmod = {
    val result = "lsmod" #| "grep nvidia" ! emptyProcessLogger
    logger.debug(s"Exit code for searching nvidia module: $result")

    if (0 == result) {
      logger.info("Removing modules...")
      "rmmod nvidia_modeset" !

      "rmmod nvidia" !
    }
  }

  def turnOffBbswitch = {
    val bbswitchContent = Source.fromFile(bbswitchPath).mkString.trim
    logger.debug(bbswitchContent)

    if (bbswitchContent.endsWith("ON")) {
      logger.info("Switching bbswitch OFF")
      "echo OFF" #> new File(bbswitchPath) !
    }
  }
}

object NvidiaScourer {
  val Tick = "tick"
}
