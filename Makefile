FrontendTests.class: FrontendTests.java 
	make clean
	javac -cp $(HOME)/junit5.jar:. FrontendTests.java

startServer: WebApp.class
	java WebApp 8000

runAllTests: FrontendTests.class
	java -jar $(HOME)/junit5.jar -cp . -c FrontendTests

WebApp.class: WebApp.java FrontendTests.class
	javac -cp $(HOME)/junit5.jar:. FrontendTests.java
	javac WebApp.java

clean:
	rm -rf *.class
