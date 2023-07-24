package com.glsx.constant;

public interface Constants {
    // 项目配置相关
    String THREAD_SIZE = "thread.size";

    // MYSQL相关
    String JDBC_DRIVER = "jdbc.driver";
    String JDBC_DATASOURCE_SIZE = "jdbc.datasource.size";
    String JDBC_URL = "jdbc.url";
    String JDBC_USER = "jdbc.user";
    String JDBC_PASSWORD = "jdbc.password";

    // Kafka相关
    String BOOTSRAO_SERVERS = "bootstrap.servers";
    String ZOOKEEPER_CONNCT = "zookeeper.connect";
    String METADATA_BROKER_LIST = "metadata.broker.list";


    String KAFKA_GROUP_ID = "kafka.group.id";

    String SOURCE_TOPIC_NAME = "source.topic.name";
    String SINK_TOPIC_NAME = "sink.topic.name";

    String SOURCE_TOPIC_NUM = "source.topic.num";
    String SINK_TOPIC_NUM = "sink.topic.num";

    // HBase相关
    String HBASE_ZOOKEEPER_QUORUM = "hbase.zookeeper.quorum";
    String HBASE_TABLE_GPS = "hbase.table.gps";
    String HBASE_TABLE_LBS = "hbase.table.lbs";

    // LBS数据点稀释间隔（S）
    String LBS_DILUTION_INTERVAL = "lbs.dilution.interval";
}
