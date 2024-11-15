package hotel.management;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Branch {
    int branchId;
    List<Room> rooms;

    public Branch(){
        rooms=new ArrayList<>();
    }
}
