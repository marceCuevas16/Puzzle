package com.example.marce.luckypuzzle.ui.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.marce.luckypuzzle.R;
import com.example.marce.luckypuzzle.common.LuckyActivity;
import com.example.marce.luckypuzzle.di.app.LuckyGameComponent;
import com.example.marce.luckypuzzle.di.component.ActivityComponent;
import com.example.marce.luckypuzzle.ui.recyclerViews.adapters.PictureGalleryAdapter;
import com.example.marce.luckypuzzle.ui.recyclerViews.itemDecoration.SquareGridSpacingItemDecoration;
import com.example.marce.luckypuzzle.ui.recyclerViews.viewHolders.GridLayoutAdapter;
import com.example.marce.luckypuzzle.ui.recyclerViews.viewHolders.ItemTouchHelperAdapter;
import com.example.marce.luckypuzzle.ui.recyclerViews.viewHolders.SimpleItemTouchHelperCallback;
import com.example.marce.luckypuzzle.utils.ImagePicker;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChoosePictureActivity extends LuckyActivity
        implements NavigationView.OnNavigationItemSelectedListener,PictureGalleryAdapter.ItemListener {

    private static final int PICK_IMAGE_ID = 1;
    private RecyclerView mRecyclerView;
    private PictureGalleryAdapter adapter;
    private ArrayList<Integer> imageListId;
    private Bitmap photo;
    private boolean userImageSelected,ourImageSelected;
    private int imageId;
    private String uriString,uriPictureTaken;
    private Uri uri;
    private int spanCount=2;
    private String profileUri;
    private String userName;
    @BindView(R.id.imagePreview)ImageView imagePreview;
    @BindView(R.id.textInsideImage)TextView gridSize;
    @BindView(R.id.seekBar)SeekBar seekBar;


    @Override
    protected int getLayout() {
        return R.layout.activity_choose_picture;
    }

    @Override
    protected void setupActivityComponent(LuckyGameComponent appComponent) {

    }

    @Override
    protected ActivityComponent getComponent() {
        return null;
    }

    @Override
    protected void init() {
        ButterKnife.bind(this);
        if(getIntent().getStringExtra("uri")!=null)
            uriString= getIntent().getStringExtra("uri");
        else
            uriString=getIntent().getStringExtra("imageURL");
        userName= getIntent().getStringExtra("userName");
        imageListId = new ArrayList<>();
        Field[] drawables = R.drawable.class.getFields();
        for (Field f : drawables) {
            if (f.getName().startsWith("pic"))
                imageListId.add(getResources().getIdentifier(f.getName(), "drawable", getPackageName()));
        }

        mRecyclerView= (RecyclerView) findViewById(R.id.recyclerGallery);
        adapter= new PictureGalleryAdapter(this,imageListId,this);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.addItemDecoration(new SquareGridSpacingItemDecoration(this,R.dimen.brick_divider_width,3));


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.choseAPicture);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View hView =  navigationView.getHeaderView(0);
        CircleImageView profileImage= (CircleImageView) hView.findViewById(R.id.profileImage);
        TextView user= (TextView) hView.findViewById(R.id.userName);
        user.setText(userName);
        Picasso.with(this).load(uriString).resize(128,128).into(profileImage);

        navigationView.setNavigationItemSelectedListener(this);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                spanCount=progress+2;
                gridSize.setText(String.valueOf(spanCount)+"x"+String.valueOf(spanCount));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onItemSelected(int position) {
        Picasso.with(this).load(imageListId.get(position)).resize(240,240).into(imagePreview);
        ourImageSelected=true;
        userImageSelected=false;
        imageId=imageListId.get(position);
    }

    @OnClick({R.id.browse,R.id.start,R.id.random})
    public void OnClick(View v){
        switch (v.getId()){
            case R.id.browse:
                showPictureDialog();
                break;
            case R.id.random:
                showRandomImage();
                break;
            case R.id.start:
                startGame();
                break;
        }
    }

    private void showRandomImage(){
        ourImageSelected=true;
        userImageSelected=false;
        Random randomGenerator = new Random();
        int random= randomGenerator.nextInt(imageListId.size());
        imageId=imageListId.get(random);
        Picasso.with(this).load(imageListId.get(random)).resize(240,240).into(imagePreview);
    }

    private void startGame() {
        if((userImageSelected||ourImageSelected)){
            Intent intent= new Intent(this,HomeActivity.class);
            if(userImageSelected)
                intent.putExtra("uri",uri);
            else if(ourImageSelected)
                intent.putExtra("imageId",imageId);
            intent.putExtra("spanCount",spanCount);
            intent.putExtra("arrayImages",imageListId);
            intent.putExtra("userName",userName);
            intent.putExtra("profileUri",uriString);
            startActivity(intent);
            finish();
        }
    }

    private void showPictureDialog() {
        Intent chooseImageIntent = ImagePicker.getPickImageIntent(this);
        startActivityForResult(chooseImageIntent, PICK_IMAGE_ID);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_ID && resultCode == RESULT_OK){
            photo=ImagePicker.getImageFromResult(this,resultCode,data);
            uriPictureTaken= ImagePicker.getImageUri(this,photo).toString();
            Picasso.with(this).load(uriPictureTaken).resize(240,240).into(imagePreview);
            ourImageSelected=false;
            userImageSelected=true;
        }
    }
}
