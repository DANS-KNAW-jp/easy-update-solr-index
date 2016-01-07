/**
 * Copyright (C) 2015-2016 DANS - Data Archiving and Networked Services (info@dans.knaw.nl)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.knaw.dans.easy.solr

import java.io.{ByteArrayOutputStream, File}
import java.lang.System.clearProperty

import nl.knaw.dans.easy.solr.CustomMatchers._
import org.apache.commons.configuration.PropertiesConfiguration
import org.apache.commons.io.FileUtils
import org.scalatest.{FlatSpec, Matchers, OneInstancePerTest}

import scala.collection.JavaConverters._

class ConfSpec extends FlatSpec with Matchers {

  clearProperty("app.home")
  def helpInfo = {
    val mockedStdOut = new ByteArrayOutputStream()
    Console.withOut(mockedStdOut) {
      new Conf().printHelp()
    }
    mockedStdOut.toString
  }

  "options in help info" should "be part of README.md" in {
    val options = helpInfo.split("Options:")(1)
    new File("README.md") should containTrimmed(options)
  }

  "synopsis in help info" should "be part of README.md" in {
    val synopsis = helpInfo.split("Options:")(0).split("Usage:")(1)
    new File("README.md") should containTrimmed(synopsis)
  }

  "first banner line" should "be part of README.md and pom.xml" in {
    val description = helpInfo.split("\n")(1)
    new File("README.md") should containTrimmed(description)
    new File("pom.xml") should containTrimmed(description)
  }

  "distributed default properties" should "be valid options" in {
    val optKeys = new Conf().builder.opts.map(opt => opt.name).toArray
    val propKeys = new PropertiesConfiguration("src/main/assembly/dist/cfg/application.properties")
      .getKeys.asScala.withFilter(key => key.startsWith("default.") )

    propKeys.foreach(key => optKeys should contain (key.replace("default.","")) )
  }
}