package org.example;

import java.io.IOException;

public class Origin extends Destination{
    public Origin(String destinationType, String address, String countyName) throws IOException {
        super(destinationType, address, countyName);
    }
}
