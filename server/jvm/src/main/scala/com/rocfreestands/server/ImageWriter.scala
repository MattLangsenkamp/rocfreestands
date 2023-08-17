package com.rocfreestands.server

import java.nio.file.{Files, Path}
import java.nio.file.OpenOption
import cats.effect.IO

import java.util.UUID

class ImageWriter(baseDir: Path):

  def writeImage(uuid: String, base64Image: String): IO[Path] =
    IO.blocking {
      val filePath = Path.of(baseDir.toString, f"$uuid.txt")
      Files.write(filePath, base64Image.getBytes)
    }

  def deleteImage(uuid: String): IO[Unit] =
    IO.blocking {
      val filePath = Path.of(baseDir.toString, f"$uuid.txt")
      Files.delete(filePath)
    }
