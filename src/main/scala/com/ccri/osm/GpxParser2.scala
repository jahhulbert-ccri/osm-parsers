package com.ccri.osm

import java.io._
import java.util.UUID
import java.util.concurrent.atomic.{AtomicBoolean, AtomicInteger}
import java.util.concurrent.{ArrayBlockingQueue, Executors, TimeUnit}
import java.util.zip.GZIPOutputStream

class GpxParser2() {
  def parse(is: InputStream, out: Output): Unit = {
      val rootElem = scala.xml.XML.load(is)
      (rootElem \ "trk").foreach { trk =>
        val trkId = UUID.randomUUID().toString
        (trk \ "trkseg").foreach { trkseg =>
          (trkseg \ "trkpt").foreach { trkpt =>
            val lat = (trkpt \ "@lat").text
            val lon = (trkpt \ "@lon").text
            val ele = (trkpt \ "ele").text
            val time = (trkpt \ "time").text
            //if (time != "") {
              out.output(List(trkId, lat, lon, ele, time).map("\"" + _ + "\"").mkString(","))
            //}
          }
        }
      }
  }

}

trait Output {
  def output(s: String)
}

class BigWriter(outDir: File) extends Output {
  val q = new ArrayBlockingQueue[String](100000)

  class WriteThread(outDir: File) extends Runnable {
    val shutdown = new AtomicBoolean(false)
    val nextNum = new AtomicInteger(0)

    def nextWriter(): PrintWriter = {
      val next = new File(outDir, f"${nextNum.getAndIncrement()}%03d" + ".csv.gz")
      next.createNewFile()
      val pw = new PrintWriter(new GZIPOutputStream(new FileOutputStream(next)))

      // header if desired
      //pw.println(List("trkid", "lat", "lon", "ele", "time").map("\"" + _ + "\"").mkString(","))

      pw
    }

    override def run(): Unit = {
      var numWritten = 0
      var wr = nextWriter()
      while(!shutdown.get()) {
        wr.println(q.poll(1, TimeUnit.SECONDS))
        numWritten = numWritten + 1
        if (numWritten % 15000000 == 0) {
          wr.close()
          wr = nextWriter()
        }
      }
      wr.close()
    }
  }

  val w = new WriteThread(outDir)
  val exsvc = Executors.newFixedThreadPool(1)
  exsvc.submit(w)
  exsvc.shutdown()

  override def output(s: String): Unit ={
    q.put(s)
  }

  def shutdown(): Unit = {
    w.shutdown.set(true)
  }
}

object GpxParser2 {
  def main (args: Array[String]) {
//    val inDir = "/home/ahulbert/dev/data/osm/gpx-planet-2013-04-09/trackable"
//    val outDir = new File("/home/ahulbert/dev/data/osm/csv/trackable")
//    val inDir = "/home/ahulbert/dev/data/osm/gpx-planet-2013-04-09/identifiable"
//    val outDir = new File("/home/ahulbert/dev/data/osm/csv/identifiable")
    val inDir = "/home/ahulbert/dev/data/osm/gpx-planet-2013-04-09/public"
    val outDir = new File("/home/ahulbert/dev/data/osm/csv/public")

    val log = new File("/tmp/gpx."+System.currentTimeMillis().toString + ".log")
    log.createNewFile()
    val logPr = new PrintWriter(log)
    val wr = new BigWriter(outDir)
    val parser = new GpxParser2()

    def recurse(start: File) {
      start.listFiles.foreach { f =>
        if (f.isDirectory) { recurse (f) }
        else if (f.isFile && f.getName.endsWith(".gpx")) {
          logPr.println("parsing " + f.getPath)
          val fis = new FileInputStream(f)
          try {
            parser.parse(fis, wr)
          } finally {
            fis.close()
          }
        }
      }
    }

    try {
      recurse(new File(inDir))
    } finally {
      logPr.close()
      wr.shutdown()
    }
  }
}
