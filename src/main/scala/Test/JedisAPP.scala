package Test

import util.{ConnectPoolUtils, JedisConnectionPool}
import org.apache.spark.rdd.RDD

/**
  * 指标统计
  */
object JedisAPP {

  // 指标1
  def Result01(lines:RDD[(String, List[Double])]): Unit ={
    lines.foreachPartition(f=>{
      val jedis = JedisConnectionPool.getConnection()
      f.foreach(t=>{
        // 充值订单数
        jedis.hincrBy(t._1,"count",t._2.head.toLong)
        // 充值金额
        jedis.hincrByFloat(t._1,"money",t._2(1))
        // 充值成功数
        jedis.hincrBy(t._1,"success",t._2(2).toLong)
        // 充值总时长
        jedis.hincrBy(t._1,"time",t._2(3).toLong)
      })
      jedis.close()
    })
  }
  //指标2
  def Result02(lines: RDD[(String, Double)]): Unit ={
    lines.foreachPartition(f=>{
      val jedis = JedisConnectionPool.getConnection()
      f.foreach(t=>{
       jedis.incrBy(t._1,t._2.toLong)
      })
      jedis.close()
    })
  }
  // 指标二
  def Result03(lines:RDD[((String, String), List[Double])]): Unit ={
    lines.foreachPartition(f=>{
      // 获取连接
      val conn = ConnectPoolUtils.getConnections()
      // 处理数据
      f.foreach(t=>{
        val sql = "insert into ProvHour(prov,hour,counts) " +
          "values('"+t._1._1+"','"+t._1._2+"',"+(t._2(0)-t._2(2))+")"
        val state = conn.createStatement()
        state.executeUpdate(sql)
      })
      // 还链接
      ConnectPoolUtils.resultConn(conn)
    })
  }
  //指标三
  def Result04(lines:RDD[((String,String),List[Double])]): Unit ={
    lines.foreachPartition(f=>{
      val coon = ConnectPoolUtils.getConnections()
      f.foreach(t=>{
        //处理数据
        val rate = ((t._2(2).toDouble)/(t._2.head.toDouble)*100).formatted("%.1f").toString
        val sql = "insert into provtop (hour,prov,total_count,success_count) "+
          "values ( ' "+t._1._1+" ',' "+t._1._2+" ', ' "+t._2.head+" ',  "+rate+") ;"
        val state = coon.createStatement()
        state.execute(sql)
      })
      // 还链接
      ConnectPoolUtils.resultConn(coon)
    })

  }
  //指标四
  def Result05 (lines:RDD[(String,List[Double])]): Unit ={
      val coon = ConnectPoolUtils.getConnections()
      lines.foreachPartition(f=>{
      f.foreach(t=>{
        val sql = "insert into hourconmon \\(hour\\,success_count\\,money\\) values \\( "+t._1+"\\,"+t._2(2)+"\\,"+t._2(1)+"\\)"+"\\;"
        val state = coon.createStatement()
        state.execute(sql)
      })
        // 还链接
        ConnectPoolUtils.resultConn(coon)
  })
  }
}
