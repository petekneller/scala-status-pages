package com.github.petekneller.statuspages

import java.lang.management.ManagementFactory
import java.util.concurrent.atomic.AtomicReference
import java.util.{Timer, TimerTask}

import org.joda.time.{LocalDateTime, LocalTime}

import scala.concurrent.duration.{Duration, _}
import scalaz.syntax.std.boolean._

trait Status {
  def name: String
  def healthy: Boolean
}
case class SingleStatus(name: String, description: String, value: String, healthy: Boolean) extends Status
case class CompositeStatus(name: String, statii: Seq[Status]) extends Status {
  def healthy: Boolean = statii.forall(_.healthy)
}

trait Check { def currentValue: (String, Boolean) }
object Check { def apply(check: => (String, Boolean)): Check = new Check{ def currentValue = check } }

sealed trait StatusCheck {
  def currentValue: Status
}
case class SingleCheck(name: String, description: String, check: Check) extends StatusCheck {
  def currentValue: Status = {
    val (value, health) = check.currentValue
    SingleStatus(name, description, value, health)
  }
}
case class CompositeCheck(name: String, checks: Seq[StatusCheck]) extends StatusCheck {
  def currentValue: Status = CompositeStatus(name, checks.map(_.currentValue))
}
object InfoCheck { def apply(name: String, description: String, check: => String): SingleCheck = SingleCheck(name, description, new Check { def currentValue = (check, true) }) }
object InfoConstant { def apply(name: String, description: String, value: String): SingleCheck = SingleCheck(name, description, new Check { val currentValue = (value, true) }) }

object StatusChecks {
  def composite(name: String, checks: StatusCheck*): CompositeCheck = CompositeCheck(name, checks)
  def currentValue(checks: Seq[StatusCheck]): Seq[Status] = checks.map(_.currentValue)

  def version(v: String): StatusCheck = InfoConstant("version", "Application version", v)
  def startTime(timestamp: String = LocalDateTime.now.toString): StatusCheck = InfoConstant("startTime", "When this process spun up", timestamp)
  private def runtimeName: (String, String) = {
    val rn = ManagementFactory.getRuntimeMXBean.getName.split("@")
    (rn.headOption.getOrElse("unavailable"), rn.drop(1).headOption.getOrElse("unavailable"))
  }
  def hostname: StatusCheck = InfoConstant("hostname", "Host", runtimeName._2)
  def pid: StatusCheck = InfoConstant("pid", "PID", runtimeName._1)
  def fetchTime: StatusCheck = InfoCheck("fetchTime", "Time at which this page was generated", LocalDateTime.now.toString)

  def throttled(check: Check, delay: Duration, appendTimestamp: Boolean = true): Check = {
    val latestValue: AtomicReference[(String, Boolean)] = new AtomicReference[(String, Boolean)]("initial value" -> false)
    val timer = new Timer(true)
    def run = new TimerTask {
      override def run(): Unit = {
        val (msg, health) = check.currentValue
        latestValue.set(appendTimestamp.fold(s"$msg (last update: ${LocalTime.now.toString})", msg) -> health)
        schedule(delay)
      }
    }
    def schedule(nextDelay: Duration): Unit = try {
      timer.schedule(run, nextDelay.toMillis)
    } catch {
      case thr: Throwable => latestValue.set(s"something went wrong: ${thr.toString}" -> false)
    }

    schedule(0 seconds)
    Check{ latestValue.get }
  }
}
