#!/bin/bash
run:
	@echo "Compiling..."
	javac server.java
	javac client.java
	@echo "Compiling done"

clean:
	@echo "Cleaning..."
	rm server.class
	rm client.class
	@echo "Cleaning done"
