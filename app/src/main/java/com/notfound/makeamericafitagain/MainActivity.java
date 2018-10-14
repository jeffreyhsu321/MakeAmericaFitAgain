package com.notfound.makeamericafitagain;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Debug;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.StringTokenizer;

import clarifai2.api.ClarifaiBuilder;
import clarifai2.api.ClarifaiClient;
import clarifai2.api.request.model.PredictRequest;
import clarifai2.dto.input.ClarifaiInput;
import clarifai2.dto.model.Model;
import clarifai2.dto.model.output.ClarifaiOutput;
import clarifai2.dto.prediction.Concept;

public class MainActivity extends AppCompatActivity implements
        View.OnClickListener{

    //declarations
    ImageButton btn_determine;
    ImageButton btn_picture;
    ImageButton btn_profile;
    ImageButton btn_snap;
    ImageView iv_test;

    static Bitmap imageBitmap;

    private Context mContext;

    static final int REQUEST_IMAGE_CAPTURE = 1;

    static List<Food> list_foods;

    String mCurrentPhotoPath;

    ProgressDialog dialog;

    static List<Food> list_food_cal;
    int calsToBeConsumed = 0;

    //saving photo to Firebase
    Uri photoURI;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    FirebaseStorage storage;
    StorageReference storageRef;
    DatabaseReference refRoot;
    DatabaseReference refUser;

    StorageReference foodRef;
    StorageReference foodImageRef;
    UploadTask uploadTask;

    boolean doneUpdateCal = false;


    boolean hasMadeMeal = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //init
        btn_determine = findViewById(R.id.btn_determine);
        btn_picture = findViewById(R.id.btn_picture);
        btn_profile = findViewById(R.id.btn_profile);
        btn_snap = findViewById(R.id.btn_snap);
        iv_test = findViewById(R.id.iv_test);
        mContext = this.getApplicationContext();
        dialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        refRoot = FirebaseDatabase.getInstance().getReference();
        refUser = refRoot.child(mUser.getUid());

        foodRef = storageRef.child("food.jpg");
        foodImageRef = storageRef.child("images/food.jpg");


        //attach listener
        btn_determine.setOnClickListener(this);
        btn_profile.setOnClickListener(this);
        btn_picture.setOnClickListener(this);
        btn_snap.setOnClickListener(this);


        //JASON'S FOOD/CAL DATA
        try {
            AssetManager assetManager = getAssets();

            InputStream in = assetManager.open("Temp.csv");
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            StringTokenizer st = null;
            ArrayList<Food> foodList = new ArrayList<Food>();
            while (br.ready()) {
                try {
                    st = new StringTokenizer(br.readLine(), ",");
                    String name = st.nextToken().split("\\(")[0];
                    String calories = st.nextToken().split(" ")[0];
                    Log.d("filesbla", name + " " + calories);
                    Food f = new Food(name, calories);
                    foodList.add(f);
                } catch (NoSuchElementException e) {
                    continue;
                } catch (NumberFormatException e) {
                    continue;
                }
            }
            refRoot.child("masterlist").setValue(foodList);
            Log.d("filesbla", "Successful");
        }
        catch(IOException e)
        {
            Log.d("testing", e.toString());
        }

        //grab food list cal
        refRoot.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list_food_cal = new ArrayList<>();
                for(int i = 0; i < dataSnapshot.child("masterlist").getChildrenCount(); i++){
                    String name = dataSnapshot.child("masterlist").child(String.valueOf(i)).child("name").getValue(String.class);
                    String calories = dataSnapshot.child("masterlist").child(String.valueOf(i)).child("calories").getValue(String.class);
                    Food food = new Food(name, calories);
                    list_food_cal.add(food);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }

        });
    }


    /**
     * retrieve result (List<Concepts> from BackgroundNetworking)
     * @param result
     */
    public void retrieveResult(List<ClarifaiOutput<Concept>> result) {

        createFoodList(result);
        dialog.dismiss();

        //upload
        uploadImage(iv_test);

        //intent to result activity
        dialog.dismiss();
        Intent i_result = new Intent(this, ResultActivity.class);
        startActivity(i_result);

    }


    /**
     * convert List of concepts (results) into a List of Food objects
     */
    public void createFoodList(List<ClarifaiOutput<Concept>> result){

        //init
        list_foods = new ArrayList<Food>();

        //conversion and filling
        int list_size = result.get(0).data().size();
        for(int i = 0; i < 5; i++){
            //instantiate food object and append
            list_foods.add(new Food(result.get(0).data().get(i).name(), "100"));
        }

    }


    /**
     * Convert camera image to File and pass it through Clarifai
     */
    public class BackgroundNetworking extends AsyncTask<Void, Void, List<ClarifaiOutput<Concept>>> {

        @Override
        protected  void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(List<ClarifaiOutput<Concept>> concept){
            retrieveResult(concept);
        }

        @Override
        protected  List<ClarifaiOutput<Concept>> doInBackground(Void... params){

            //Convert bitmap to File
            try{
                File outputDir = mContext.getCacheDir(); // context being the Activity pointer
                File outputFile = File.createTempFile("img_", ".jpg", outputDir);
                OutputStream os = new BufferedOutputStream(new FileOutputStream(outputFile));
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                os.close();


                //Clarifai
                final String apiKey = "435aa8b16a7e4f8a9e90d3f44455dfd7";
                final ClarifaiClient client = new ClarifaiBuilder(apiKey).buildSync();
                new ClarifaiBuilder(apiKey).buildSync();

                Model<Concept> foodModel = client.getDefaultModels().foodModel();

                PredictRequest<Concept> request = foodModel.predict().withInputs(
                        ClarifaiInput.forImage(outputFile)
                );
                List<ClarifaiOutput<Concept>> result = request.executeSync().get();

                dialog.dismiss();
                return result;


            } catch(IOException ie) {}

            //NONRUNNING CODE
            final String apiKey = "435aa8b16a7e4f8a9e90d3f44455dfd7";
            final ClarifaiClient client = new ClarifaiBuilder("435aa8b16a7e4f8a9e90d3f44455dfd7").buildSync();
            new ClarifaiBuilder(apiKey).buildSync();
            Model<Concept> foodModel = client.getDefaultModels().foodModel();
            PredictRequest<Concept> request = foodModel.predict().withInputs(
                    ClarifaiInput.forImage("")
            );
            List<ClarifaiOutput<Concept>> result = request.executeSync().get();
            return result;
        }

    }


    /**
     * camera activity
     */
    private void dispatchTakePictureIntent() {
        doneUpdateCal = false;

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(this, "Error taking photo. Please contact support", Toast.LENGTH_SHORT).show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(this,
                        "com.notfound.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    /**
     * save and set image from camera
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            iv_test.setImageBitmap(imageBitmap);
        }*/
        setPic();
    }

    /**
     * Creates the full res image
     * @return
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public void onPause(){
        super.onPause();
        dialog.dismiss();
    }

    private void setPic() {
        // Get the dimensions of the View
        int targetW = iv_test.getWidth();
        int targetH = iv_test.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        Matrix matrix = new Matrix();
        matrix.postRotate(90);

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap , 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        iv_test.setImageBitmap(rotatedBitmap);
        imageBitmap = rotatedBitmap;
        dialog.dismiss();

        btn_determine.setVisibility(View.VISIBLE);
        btn_determine.bringToFront();
        btn_profile.bringToFront();
        btn_snap.bringToFront();
        btn_picture.bringToFront();
    }


    /**
     * upload image to firebase
     * @param view
     */
    public void uploadImage(View view) {

        //upload image
        StorageReference riversRef = storageRef.child("images/"+photoURI.getLastPathSegment());
        uploadTask = riversRef.putFile(photoURI);

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(getApplicationContext(), "Upload failed. Please contact support", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
                Toast.makeText(getApplicationContext(), "Upload successful!", Toast.LENGTH_SHORT).show();
            }
        });



            //COMPARE CAL VALUES
            //retrieve cal values
            Random rand = new Random();
            for(int i = 0; i < list_foods.size(); i++){
                Food food = list_foods.get(i);
                //iterate through masterlist
                for(Food food_other : list_food_cal){
                    if(food.equals(food_other, true) && !food_other.getCalories().equals("")){
                        food.setCalories(food_other.getCalories());
                        list_foods.set(i, food);
                        calsToBeConsumed += Integer.parseInt(food.getCalories());
                    } else {
                    }
                }
            }
            for(int i = 0; i < list_foods.size(); i++){
                Food food = list_foods.get(i);
                //iterate through to fill in random values
                if(food.getCalories().equals("100")){
                    int n = rand.nextInt(50) + 1;
                    food.setCalories(String.valueOf(n));
                    list_foods.set(i, food);
                    calsToBeConsumed += n;
                }
            }

            //update cal values for user
            refUser.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(!doneUpdateCal){
                        int cal = Integer.parseInt(dataSnapshot.child("calorie_today").getValue(String.class));
                        cal += calsToBeConsumed;
                        refUser.child("calorie_today").setValue(String.valueOf(cal));
                    }

                    doneUpdateCal = true;
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }

            });



        hasMadeMeal = false;

        //update database markers
        refUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(hasMadeMeal){
                    return;
                }

                //create new meal object
                Meal meal = new Meal(list_foods, Integer.toString((int)dataSnapshot.child("meals").getChildrenCount()+1), photoURI.getLastPathSegment());
                String index = meal.getIndex();
                String image_name = meal.getImage_name();

                //fill out meal object in firebase
                DatabaseReference refMeal = refUser.child("meals").child(index);
                refMeal.setValue(list_foods);
                refUser.child("meals").child(index).child("image_name").setValue(image_name.replace(".jpg",""));

                hasMadeMeal = true;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Error occurred. Please contact support", Toast.LENGTH_SHORT).show();
                return;
            }
        });
    }


    public void onClick(View v){
        switch(v.getId()){

            case R.id.btn_determine:

                //start dialog
                dialog.setMessage("Feeding Clarifai...");
                dialog.show();

                //send image to Clarifai
                new BackgroundNetworking().execute();

                break;
            case R.id.btn_picture:
                dispatchTakePictureIntent();
                break;
            case R.id.btn_profile:
                dialog.dismiss();
                Intent i_profile = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(i_profile);
                break;

            case R.id.btn_snap:
                Toast.makeText(getApplicationContext(), "SNAPKIT Integration Coming Soon!", Toast.LENGTH_SHORT).show();
                break;

            default:
                break;

        }
    }
}
