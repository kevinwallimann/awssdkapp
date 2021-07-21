package com.github.kevinwallimann

import org.scalatest.flatspec.AnyFlatSpec

class MyFirstTest extends AnyFlatSpec {
  behavior of "Spark"

  it should "not die" in {
    succeed
  }
}