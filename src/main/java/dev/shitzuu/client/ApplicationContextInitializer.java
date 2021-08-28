package dev.shitzuu.client;

public class ApplicationContextInitializer {

    private ApplicationContextInitializer() {

    }

    public static void main(String[] arguments) {
        ApplicationContext applicationContext = new ApplicationContext();
        applicationContext.initialize();
    }
}
