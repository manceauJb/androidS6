package com.example.annoncetp1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

public class ViewPagerAdapter extends PagerAdapter {
    private Context context;
    private List<String> imageUrls;
    private boolean save;

    /**
     * Adapter pour Afficher les images de l'annonce.
     * @param context context
     * @param imageUrls list d'image
     * @param save Bool pour savoir si l'annonce est dans la base ou provient de l'API.
     */
    public ViewPagerAdapter(Context context,List<String> imageUrls,boolean save){
        this.context = context;
        this.imageUrls = imageUrls;
        this.save = save;
    }

    @Override
    public int getCount() {
        if(imageUrls.size()==0){
            return 1;
        }
        return imageUrls.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    /**
     * Rempli le container avec les images (ImageView).
     * @param container container
     * @param position position de l'image
     * @return ImageView
     */
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ImageView imageView = new ImageView(context);
        if(!save) {
            // Annonce provenant de l'API.
            if (imageUrls.size() == 0) {
                // Annonce n'as pas d'image.
                Picasso.get().load("www.google.com/image/1").placeholder(R.drawable.icon_noimage).error(R.drawable.icon_noimage).into(imageView);
                container.addView(imageView);
            } else {
                // Annonce as des images.

                // On recupere l'url de l'image en position 'position'.
                String url = imageUrls.get(position);

                // Bug avec les images contenant http.
                if (!url.contains("https")) {
                    url = url.replace("http", "https");
                }

                // Charge l'image dans ImageView.
                Picasso.get()
                        .load(url)
                        .error(R.drawable.icon_errorimage)
                        .into(imageView);

                // On ajoute l'ImageView au container.
                container.addView(imageView);
            }
        }else{
            // Annonce provenant de la base.
            if(imageUrls == null | imageUrls.size()==0){
                // List null, ou pas d'image.

                // On charge une image par default dans ImageView.
                Picasso.get().load("www.google.com/image/1").placeholder(R.drawable.icon_noimage).error(R.drawable.icon_noimage).into(imageView);

                // On ajoute l'ImageView au container.
                container.addView(imageView);
            }else if(imageUrls.get(position)!=null){
                // L'annonce possede une/des image(s).
                // L'annonce etant sauvegarde les images ce trouve donc dans l'appareil.
                try{
                    // On charge le ficher en recuperant le chemin.
                    File f = new File(imageUrls.get(position));

                    // On transforme le fichier en Bitmap pour pouvoir l'afficher.
                    Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));

                    // On ajoute le Bitmap a l'imageView.
                    imageView.setImageBitmap(b);
                }catch (FileNotFoundException e){
                    e.getMessage();
                }
                // On ajoute l'ImageView au container.
                container.addView(imageView);
            }else{

                // Erreur dans la list.
                Picasso.get().load("www.google.com/image/1").placeholder(R.drawable.icon_errorimage).error(R.drawable.icon_errorimage).into(imageView);
                container.addView(imageView);
            }
        }
        return imageView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
