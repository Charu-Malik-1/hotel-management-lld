package hotel.management;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Hotel {
    int id;
    List<Branch> branches;

    public Hotel(){
        branches=new ArrayList<>();
    }
}
