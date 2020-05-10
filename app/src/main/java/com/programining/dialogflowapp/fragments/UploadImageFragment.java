package com.programining.dialogflowapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.FaceAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.programining.dialogflowapp.R;
import com.programining.dialogflowapp.models.MyConstants;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class UploadImageFragment extends Fragment implements ChooseDialogFragment.ChooseDialogInterface {
    private static final int KEY_PICK_IMAGE = 100;
    private static final int KEY_CAPTURE_IMAGE = 200;
    private static final String KEY_TAG = "UploadImageFragment";
    Bitmap mImageBitmap;
    private Context mContext;
    private Uri mImageUri;
    private ImageView ivImg;
    private TextView tvResults;
    private TextView tvDetectionLabel;
    private int mCurrentSelectedDetection;

    public UploadImageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);// in order to display options icon
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.fragment_upload_image, container, false);
        ivImg = parentView.findViewById(R.id.iv_img);
        tvResults = parentView.findViewById(R.id.tv_results);
        tvDetectionLabel = parentView.findViewById(R.id.tv_label);
        Button btnChoose = parentView.findViewById(R.id.btn_choose);
        Button btnUpload = parentView.findViewById(R.id.btn_upload);
        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayChooseDialogFragment();
            }
        });
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prepareVisionCall();
            }
        });
        return parentView;
    }

    /**
     * this function will check if mImageBitmap is null. Additionally, will handel IOException thrown by uploadImageToVision() function
     */
    private void prepareVisionCall() {
        if (mImageBitmap == null) {
            Toast.makeText(mContext, "Please select an image", Toast.LENGTH_SHORT).show();
        } else {
            try {
                uploadImageToVision();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Create options menu on the ActionBar
     *
     * @param menu
     * @param inflater
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_detect, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * Handel on menu item Selected
     * Here we enable user to select the detection type! i.e : which data the user want extract from the image
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.opt_text_detection:
                tvDetectionLabel.setText(mContext.getString(R.string.menu_text_detection));
                mCurrentSelectedDetection = 0;
                return true;
            case R.id.opt_label_detection:
                tvDetectionLabel.setText(mContext.getString(R.string.menu_lable_detection));
                mCurrentSelectedDetection = 1;
                return true;
            case R.id.opt_landmark_detection:
                tvDetectionLabel.setText(mContext.getString(R.string.landmark_det));
                mCurrentSelectedDetection = 2;
                return true;
            case R.id.opt_facial_detection:
                tvDetectionLabel.setText(mContext.getString(R.string.menu_facial_detection));
                mCurrentSelectedDetection = 3;
                return true;
            case R.id.opt_logo_detection:
                tvDetectionLabel.setText(mContext.getString(R.string.menu_logo_detection));
                mCurrentSelectedDetection = 4;
                return true;
            case R.id.opt_safe_search_detection:
                tvDetectionLabel.setText(mContext.getString(R.string.menu_safe_search));
                mCurrentSelectedDetection = 5;
                return true;
            case R.id.opt_web_detection:
                tvDetectionLabel.setText(mContext.getString(R.string.menu_web_detection));
                mCurrentSelectedDetection = 6;
                return true;
            case R.id.opt_img_properties:
                tvDetectionLabel.setText(mContext.getString(R.string.menu_img_props));
                mCurrentSelectedDetection = 7;
                return true;
            case R.id.opt_obj_locale:
                tvDetectionLabel.setText(mContext.getString(R.string.menu_object_locale));
                mCurrentSelectedDetection = 8;
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * display choose dialog
     */


    private Image getBase64EncodedJpeg(Bitmap bitmap) {
        Image image = new Image();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        image.encodeContent(imageBytes);
        return image;
    }

    /**
     * upload image to vision
     */
    private void uploadImageToVision() throws IOException {
        tvResults.setText("Retrieving results from cloud");
        new AsyncTask<Object, Void, String>() {
            @Override
            protected String doInBackground(Object... params) {
                try {
                    /** OAuth Based **/
//                    GoogleCredential credential = new GoogleCredential().setAccessToken(accessToken);
//                    HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
//                    JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
//                    Vision.Builder builder = new Vision.Builder
//                            (httpTransport, jsonFactory, credential);
//                    Vision vision = builder.build();
                    /** OAuth Based **/
                    /** API KEY **/
                    Vision.Builder visionBuilder = new Vision.Builder(
                            new NetHttpTransport(),
                            new AndroidJsonFactory(),
                            null);
                    visionBuilder.setVisionRequestInitializer(
                            new VisionRequestInitializer(MyConstants.KEY_VISION_API_KEY));
                    Vision vision = visionBuilder.build();
                    /** API KEY End**/
                    List<Feature> featureList = new ArrayList<>();
                    switch (mCurrentSelectedDetection) {
                        case 0:     //Text Detection
                            Feature textDetection = new Feature();
                            textDetection.setType("TEXT_DETECTION");
                            textDetection.setMaxResults(10);
                            featureList.add(textDetection);
                            break;
                        case 1:     //Label Detection
                            Feature labelDetection = new Feature();
                            labelDetection.setType("LABEL_DETECTION");
                            labelDetection.setMaxResults(10);
                            featureList.add(labelDetection);
                            break;
                        case 2:     //Landmark Detection
                            Feature landmarkDetection = new Feature();
                            landmarkDetection.setType("LANDMARK_DETECTION");
                            landmarkDetection.setMaxResults(10);
                            featureList.add(landmarkDetection);
                            break;
                        case 3:     //Facial Detection
                            Feature desiredFeature = new Feature();
                            desiredFeature.setType("FACE_DETECTION");
                            desiredFeature.setMaxResults(10);
                            featureList.add(desiredFeature);
                        case 4:     //Logo Detection
                            Feature logoDetection = new Feature();
                            logoDetection.setType("LOGO_DETECTION");
                            logoDetection.setMaxResults(10);
                            featureList.add(logoDetection);
                            break;
                        case 5:
                            break;
                        case 6:
                            break;
                        case 7:
                            break;
                    }
                    List<AnnotateImageRequest> imageList = new ArrayList<>();
                    AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();
                    Image base64EncodedImage = getBase64EncodedJpeg(mImageBitmap);
                    annotateImageRequest.setImage(base64EncodedImage);
                    annotateImageRequest.setFeatures(featureList);
                    imageList.add(annotateImageRequest);
                    BatchAnnotateImagesRequest batchAnnotateImagesRequest =
                            new BatchAnnotateImagesRequest();
                    batchAnnotateImagesRequest.setRequests(imageList);
                    Vision.Images.Annotate annotateRequest =
                            vision.images().annotate(batchAnnotateImagesRequest);
                    // Due to a bug: requests to Vision API containing large images fail when GZipped.
                    annotateRequest.setDisableGZipContent(true);
                    Log.d(KEY_TAG, "sending request");
                    BatchAnnotateImagesResponse response = annotateRequest.execute();
                    return convertResponseToString(response, mCurrentSelectedDetection);
                } catch (GoogleJsonResponseException e) {
                    Log.e(KEY_TAG, "Request failed: " + e.getContent());
                } catch (IOException e) {
                    Log.d(KEY_TAG, "Request failed: " + e.getMessage());
                }
                return "Cloud Vision API request failed.";
            }

            protected void onPostExecute(String result) {
                tvResults.setText(result);
            }
        }.execute();
    }

    private String convertResponseToString(BatchAnnotateImagesResponse response, int mCurrentSelectedDetection) {
        StringBuilder message = new StringBuilder("Results:\n\n");
        switch (mCurrentSelectedDetection) {
            case 0:
                message.append("Texts:\n");
                List<EntityAnnotation> texts = response.getResponses().get(0)
                        .getTextAnnotations();
                if (texts != null) {
                    for (EntityAnnotation text : texts) {
                        message.append(String.format(Locale.getDefault(), "%s: %s",
                                text.getLocale(), text.getDescription()));
                        message.append("\n");
                    }
                } else {
                    message.append("nothing\n");
                }
                break;
            case 1:
                List<EntityAnnotation> labels = response.getResponses().get(0).getLabelAnnotations();
                if (labels != null) {
                    for (EntityAnnotation label : labels) {
                        message.append(String.format(Locale.getDefault(), "%.3f: %s",
                                label.getScore(), label.getDescription()));
                        message.append("\n");
                    }
                } else {
                    message.append("nothing\n");
                }
                break;
            case 2:
                message.append("Landmarks:\n");
                List<EntityAnnotation> landmarks = response.getResponses().get(0)
                        .getLandmarkAnnotations();
                if (landmarks != null) {
                    for (EntityAnnotation landmark : landmarks) {
                        message.append(String.format(Locale.getDefault(), "%.3f: %s",
                                landmark.getScore(), landmark.getDescription()));
                        message.append("\n");
                    }
                } else {
                    message.append("nothing\n");
                }
                break;
            case 3:
                message.append("Face Detection:\n");
                List<FaceAnnotation> faces = response.getResponses().get(0)
                        .getFaceAnnotations();
                if (faces != null) {
                    for (FaceAnnotation face : faces) {
                        message.append(String.format(Locale.getDefault(), "%s",
                                face.getJoyLikelihood()));
                        message.append("\n");
                    }
                } else {
                    message.append("nothing\n");
                }
                break;
            case 4:
                message.append("Logo Detection:\n");
                List<EntityAnnotation> logos = response.getResponses().get(0)
                        .getLogoAnnotations();
                if (logos != null) {
                    for (EntityAnnotation logo : logos) {
                        message.append(String.format(Locale.getDefault(), "%s",
                                logo.getDescription()));
                        message.append("\n");
                    }
                } else {
                    message.append("nothing\n");
                }
                break;
        }
        return message.toString();
    }

    private void displayChooseDialogFragment() {
        ChooseDialogFragment dialog = new ChooseDialogFragment();
        dialog.setChooseDialogListener(UploadImageFragment.this);
        dialog.show(getChildFragmentManager(), ChooseDialogFragment.class.getSimpleName());
    }

    @Override
    public void openGallery() {
        Intent i = new Intent();
        i.setType("image/*"); // specify the type of data you expect
        i.setAction(Intent.ACTION_GET_CONTENT); // we need to get content from another act.
        startActivityForResult(Intent.createChooser(i, "choose App"), KEY_PICK_IMAGE);
    }

    @Override
    public void openCamera() {
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, KEY_CAPTURE_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //if image from Camera
        if (requestCode == KEY_CAPTURE_IMAGE) {
            if (data == null) {
                Toast.makeText(mContext, "Unexpected Error Happened while capturing the picture!", Toast.LENGTH_SHORT).show();
            } else {
                mImageBitmap = (Bitmap) data.getExtras().get("data");
                //TODO : data.getData() return null in most devices! try to find fix!
                mImageUri = data.getData(); //data.getData() (BUG) - return null in most devices!
                ivImg.setImageBitmap(mImageBitmap);
            }
        } else if (requestCode == KEY_PICK_IMAGE) {
            if (data == null) {
                Toast.makeText(mContext, "Unexpected Error Happened while selecting  picture!", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    Uri imgUri = data.getData();//1
                    InputStream imageStream = mContext.getContentResolver().openInputStream(imgUri);//2
                    mImageBitmap = BitmapFactory.decodeStream(imageStream);//3}
                    mImageUri = imgUri;
                    ivImg.setImageBitmap(mImageBitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    //hellow


}