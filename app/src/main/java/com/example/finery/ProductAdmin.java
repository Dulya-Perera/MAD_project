package com.example.finery;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import com.example.finery.Model.Product;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ProductAdmin extends AppCompatActivity {

    //Object Declarations
    RecyclerView recyclerView;
    TextView txtID;
    Query query1;
    private DatabaseReference mdatabasereference;
    Product product;
    private ProgressDialog progressDialog;
    FirebaseRecyclerAdapter<Product, ProductViewHolder> firebaseRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_admin);

        //Progress Dialog
        progressDialog = new ProgressDialog(ProductAdmin.this);
        progressDialog.setMessage("Loading Products Please Wait...");
        progressDialog.show();

        //Connect with Database
        mdatabasereference = FirebaseDatabase.getInstance().getReference("products").child("accessories");

        recyclerView = (RecyclerView) findViewById(R.id.adminRecyclerViewGridView);
        txtID = (TextView) findViewById(R.id.adminpID);

        product = new Product();

        //Button for add new product
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProductAdmin.this, AddNewProduct.class);
                startActivity(intent);
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        //Connect with database
        query1 = FirebaseDatabase.getInstance().getReference().child("products").child("accessories");
        FirebaseRecyclerOptions<Product> options =
                new FirebaseRecyclerOptions.Builder<Product>()
                        .setQuery(query1, Product.class)
                        .build();

        //Display the data in recycleview
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Product, ProductViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ProductViewHolder productViewHolder, final int i, @NonNull Product product_get_set_v) {

                productViewHolder.setid(product_get_set_v.getId());
                productViewHolder.settitle(product_get_set_v.getTitle());
                productViewHolder.setprice(product_get_set_v.getPrice());
                productViewHolder.setoffer(product_get_set_v.getOffer());
                productViewHolder.setsize(product_get_set_v.getSize());
                productViewHolder.setcolor(product_get_set_v.getColor());
                productViewHolder.setdescription(product_get_set_v.getDescription());
                String image_url = productViewHolder.setimage(product_get_set_v.getImage());

                productViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String productid=getRef(i).getKey();

                        assert productid != null;
                        mdatabasereference.child(productid).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
                    }
                });

                //Update the product details
                productViewHolder.btnUpdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String productid = getRef(i).getKey();

                        mdatabasereference.child(productid).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                                String id = dataSnapshot.child("id").getValue(String.class);
                                String title = dataSnapshot.child("title").getValue(String.class);
                                String description = dataSnapshot.child("description").getValue(String.class);
                                String size = dataSnapshot.child("size").getValue(String.class);
                                String color = dataSnapshot.child("color").getValue(String.class);
                                String image = dataSnapshot.child("image").getValue(String.class);
                                String price = dataSnapshot.child("price").getValue(Integer.class).toString();
                                String offer = dataSnapshot.child("offer").getValue(Integer.class).toString();


                                //Display the alert dialog box to update
                                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ProductAdmin.this);
                                LayoutInflater inflater = getLayoutInflater();
                                final View dialogView = inflater.inflate(R.layout.activity_product_update, null);
                                dialogBuilder.setView(dialogView);

                                final EditText editId = (EditText) dialogView.findViewById(R.id.uProductID);
                                final EditText editTitle = (EditText) dialogView.findViewById(R.id.uProductTitle);
                                final EditText editDescription = (EditText) dialogView.findViewById(R.id.uProductDescription);
                                final EditText editColor = (EditText) dialogView.findViewById(R.id.uProductColor);
                                final EditText editSize = (EditText) dialogView.findViewById(R.id.uProductSize);
                                final EditText editPrice = (EditText) dialogView.findViewById(R.id.uProductPrice);
                                final EditText editOffer = (EditText) dialogView.findViewById(R.id.uProductOffer);
                                final EditText editImage = (EditText) dialogView.findViewById(R.id.uProductImage);
                                final Button buttonUpdate = (Button) dialogView.findViewById(R.id.updateBtn);

                                //Set the texts to display on edittext
                                editId.setText(id);
                                editTitle.setText(title);
                                editDescription.setText(description);
                                editColor.setText(color);
                                editSize.setText(size);
                                editPrice.setText(price);
                                editOffer.setText(offer);
                                editImage.setText(image);


                                dialogBuilder.setTitle("Update Product");
                                final AlertDialog alertDialog = dialogBuilder.create();
                                alertDialog.show();


                                //Connect with database to update product details
                                buttonUpdate.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        DatabaseReference updateRef = FirebaseDatabase.getInstance().getReference().child("products").child("accessories");
                                        updateRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                if (dataSnapshot.hasChild(productid)) {
                                                        product.setId(editId.getText().toString().trim());
                                                        product.setTitle(editTitle.getText().toString().trim());
                                                        product.setDescription(editDescription.toString().trim());
                                                        product.setColor(editColor.getText().toString().trim());
                                                        product.setSize(editSize.getText().toString().trim());
                                                        product.setPrice(Integer.parseInt(editPrice.getText().toString().trim()));
                                                        product.setOffer(Integer.parseInt(editOffer.getText().toString().trim()));
                                                        product.setImage(editImage.getText().toString().trim());

                                                        mdatabasereference = FirebaseDatabase.getInstance().getReference().child("products").child("accessories").child(productid);
                                                        mdatabasereference.setValue(product);

                                                        Toast.makeText(getApplicationContext(), "Updated Product", Toast.LENGTH_SHORT).show();
                                                        alertDialog.dismiss();
                                                }
                                                else
                                                    Toast.makeText(getApplicationContext(), "No source to Update", Toast.LENGTH_SHORT).show();
                                            }
                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                            }
                                        });
                                    }
                                });

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });

                    }
                });

                //Connect with database and delete the product
                productViewHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        final String productid = getRef(i).getKey();
                        DatabaseReference delRef = FirebaseDatabase.getInstance().getReference().child("products").child("accessories");
                        delRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                assert productid != null;
                                if(dataSnapshot.hasChild(productid)){
                                    mdatabasereference = FirebaseDatabase.getInstance().getReference().child("products").child("accessories").child(productid);
                                    mdatabasereference.removeValue();

                                    Snackbar.make(v, "Successfully Deleted the Product", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                                }
                                else
                                    Toast.makeText(getApplicationContext(), "No data found to delete.", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
                    }
                });
            }

            //Display the card view
            @NonNull
            @Override
            public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.activity_product_admin_card, parent, false);
                progressDialog.dismiss();
                return new ProductViewHolder(view);
            }
        };

        firebaseRecyclerAdapter.startListening();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(),1);
        recyclerView.setLayoutManager(gridLayoutManager);

        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        public ImageButton btnDelete;
        public Button btnUpdate;
        View mView;

        public ProductViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            btnDelete = (ImageButton) mView.findViewById(R.id.adminpDelete);
            btnUpdate = (Button) mView.findViewById(R.id.adminpUpdate);
        }

        public void setid(String id)
        {
            TextView pid=(TextView)mView.findViewById(R.id.adminpID);
            pid.setText(id);

        }

        public void settitle(String title)
        {
            TextView ptitle=(TextView)mView.findViewById(R.id.adminpTitle);
            ptitle.setText(title);

        }

        public void setprice(int price)
        {
            TextView pprice=(TextView)mView.findViewById(R.id.adminpPrice);
            pprice.setText(String.valueOf(price));

        }

        public void setsize(String size)
        {
            TextView psize=(TextView)mView.findViewById(R.id.adminpSize);
            psize.setText(size);

        }

        public void setcolor(String color)
        {
            TextView pcolor=(TextView)mView.findViewById(R.id.adminpColors);
            pcolor.setText(color);

        }

        public void setdescription(String description)
        {
            TextView pdescription=(TextView)mView.findViewById(R.id.adminpDescription);
            pdescription.setText(description);

        }

        public String setimage(String url)
        {
            ImageView image = (ImageView)mView.findViewById(R.id.adminpimage);
            Picasso.get().load(url).into(image);
            return url;
        }

        @SuppressLint("SetTextI18n")
        public void setoffer(int offer) {
            TextView poffer = (TextView) mView.findViewById(R.id.adminpOffer);
            poffer.setText(offer +"%");
        }
    }
}
