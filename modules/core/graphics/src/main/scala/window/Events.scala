package seer.graphics


case class KeyEvent(key:String, keycode:Int, scancode:Int, action:Int, mods:Int)
case class MouseEvent(state:MouseState)

class MouseState {
    var event = ""
    var px:Double = 0.0
    var py:Double = 0.0
    var x:Double = 0.0
    var y:Double = 0.0
    var dx:Double = 0.0
    var dy:Double = 0.0
    var button:Int = 0
    var action:Int = 0
    var mods:Int = 0
    var scrollx:Double = 0.0
    var scrolly:Double = 0.0
    var inside:Boolean = false
}
