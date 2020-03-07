module com.example.bar {
    exports com.example.bar;
    //requires org.slf4j;
    requires org.apache.logging.log4j;
    requires com.example.greeter;
}