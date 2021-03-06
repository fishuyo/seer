
package com.fishuyo.seer
package graphics

import spatial.Vec3

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20

import collection.mutable.ListBuffer

class Environment {

  var backgroundColor = RGBA(0,0,0,1)

  var fog = 0.135f
  var fogColor = RGB.black

  var blend = false
  var depth = true
  var lineWidth = 1f
  // var pointSize = 1f

  var alpha = 1f

  var lightPosition = Vec3(1,1,1)
  var lightAmbient = RGBA(.2f,.2f,.2f,1)
  var lightDiffuse = RGBA(.6f,.6f,.6f,1)
  var lightSpecular = RGBA(.4f,.4f,.4f,1)

  // var lights = ListBuffer[Light]()
  var srcBlend = GL20.GL_ONE
  var dstBlend = GL20.GL_ONE

  def default(){
    backgroundColor = RGBA(0,0,0,1)
    fog = 0.135f
    fogColor = RGB.black
    blend = false
    depth = true
    lineWidth = 1f
    alpha = 1f
    lightPosition = Vec3(1,1,1)
    lightAmbient = RGBA(.2f,.2f,.2f,1)
    lightDiffuse = RGBA(.6f,.6f,.6f,1)
    lightSpecular = RGBA(.4f,.4f,.4f,1)
    srcBlend = GL20.GL_ONE
    dstBlend = GL20.GL_ONE
  }

  def blendFunc(src:Int, dst:Int){
    srcBlend = src
    dstBlend = dst
  }

  def setGLState(){
    Gdx.gl.glClearColor(backgroundColor.r,backgroundColor.g,backgroundColor.b,backgroundColor.a)
    // Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT)

    Gdx.gl.glLineWidth(lineWidth)
    // Gdx.gl.glPointSize(pointSize)

    com.badlogic.gdx.Gdx.gl.glEnable( 0x8642 ); // enable gl_PointSize ???
    com.badlogic.gdx.Gdx.gl.glEnable( 0x2848 ); // enable gl_linesmooth
    com.badlogic.gdx.Gdx.gl.glEnable( 0x2832 ); // enable gl_linesmooth
    // GL11.glEnable(GL11.GL_LINE_SMOOTH);
    // GL11.glEnable(GL11.GL_POINT_SMOOTH);


    if(blend) Gdx.gl.glEnable(GL20.GL_BLEND)
    else Gdx.gl.glDisable( GL20.GL_BLEND )
    Gdx.gl.glBlendFunc(srcBlend, dstBlend)


    if(depth) Gdx.gl.glEnable( GL20.GL_DEPTH_TEST )
    else Gdx.gl.glDisable( GL20.GL_DEPTH_TEST )
  }
}