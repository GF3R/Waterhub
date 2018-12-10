package ch.gf3r.waterhubapp;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class WaterdataService {

    private String user = "";
    private List<Data> data;

    public WaterdataService(String user){
        this.user = user;
        data = new ArrayList<>();

    }

    public void GetWaterData(final WaterdataListener waterdataListener) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("users").child(user).child("waterdata");
        Query orderedData = database.orderByChild("date");

        orderedData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Data detail = snapshot.getValue(Data.class);
                    data.add(detail);
                }
                waterdataListener.recieveData(data);
            }


            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value

            }
        });
    }
}
