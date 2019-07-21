package util

object Test {
  def main(args: Array[String]): Unit = {
    val coon = ConnectPoolUtils.getConnections()
    val sql ="insert into provhour (prov,hour,counts) "+
      "  values ( ' "+"asc "+" ', ' "+"52"+" ',"+"52"+") ;"
    val state = coon.createStatement()
    println (state.execute(sql) )
  }
}
