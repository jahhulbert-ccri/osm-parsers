//package com.ccri.osm
//
//import java.io.InputStream
//import javax.xml.stream.{XMLStreamConstants, XMLInputFactory}
//
//class GpxParser(is: InputStream) {
//
//  def parse(): Unit ={
//    val xmlif = XMLInputFactory.newInstance()
//    val xmlReader = xmlif.createXMLStreamReader(is)
//
//    while (xmlReader.hasNext) {
//        val cur = xmlReader.next()
//        var curElementName = null
//        cur match {
//          case XMLStreamConstants.START_ELEMENT =>
//            curElementName = xmlReader.getLocalName
//            curElementName match {
//              case "trk" =>
//              case "name" =>
//              case "trkseg" =>
//              case "trkpt" =>
//                val attrCount = xmlReader.getAttributeCount
//                if (attrCount == 2) {
//                  if (xmlReader.getAttributeLocalName(0) == "lat") {
//                    val lat = xmlReader.getAttributeValue(0).toDouble
//                  }
//                  if (xmlReader.getAttributeLocalName(1) == "lon") {
//                    val lon = xmlReader.getAttributeValue(1).toDouble
//                  }
//                }
//              case "ele" =>
//            }
//          case XMLStreamConstants.END_ELEMENT =>
//            val tagName = xmlReader.getLocalName
//            tagName match {
//              case "trk" =>
//              case "name" =>
//              case "trkseg" =>
//              case "trkpt" =>
//                val attrCount = xmlReader.getAttributeCount
//                if (attrCount == 2) {
//                  if (xmlReader.getAttributeLocalName(0) == "lat") {
//                    val lat = xmlReader.getAttributeValue(0).toDouble
//                  }
//                  if (xmlReader.getAttributeLocalName(1) == "lon") {
//                    val lon = xmlReader.getAttributeValue(1).toDouble
//                  }
//                }
//              case "ele" =>
//        }
//
//    }
//  }
//
//}
