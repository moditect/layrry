module com.example.foo {
    exports com.example.foo;
    // requires org.slf4j;
    requires org.apache.logging.log4j;
    requires com.example.greeter;
}