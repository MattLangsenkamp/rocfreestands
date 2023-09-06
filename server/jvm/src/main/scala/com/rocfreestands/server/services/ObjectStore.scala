package com.rocfreestands.server.services

import cats.effect.IO

import java.nio.file.{Files, Path}

trait ObjectStore[F[_]]:

  def writeImage(uuid: String, base64Image: String): F[Path]

  def getImage(uuid: String): F[String]

  def deleteImage(uuid: String): F[Unit]


object ObjectStore:
  def makeObjectStore(baseDir: Path): IO[ObjectStore[IO]] = IO.pure {
    new ObjectStore[IO]:
      override def writeImage(uuid: String, base64Image: String): IO[Path] = IO.blocking {
        val filePath = Path.of(baseDir.toString, f"$uuid.txt")
        Files.write(filePath, base64Image.getBytes)
      }
  
      override def getImage(uuid: String): IO[String] = IO.blocking {
        val filePath = Path.of(baseDir.toString, f"$uuid.txt")
        Files.readString(filePath)
      }
  
      override def deleteImage(uuid: String): IO[Unit] = IO.blocking {
        val filePath = Path.of(baseDir.toString, f"$uuid.txt")
        Files.delete(filePath)
      }
  }
