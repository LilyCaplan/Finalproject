package com.example.shar;

import android.content.Context;
import android.net.Uri;

import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.assets.RenderableSource;

import java.lang.ref.WeakReference;
import java.util.concurrent.CompletableFuture;

/**
 * Model loader class to avoid memory leaks from the activity. Activity and Fragment controller
 * classes have a lifecycle that is controlled by the UI thread. When a reference to one of these
 * objects is accessed by a background thread it is "leaked". Using that reference to a
 * lifecycle-bound object after Android thinks it has "destroyed" it can produce bugs. It also
 * prevents the Activity or Fragment from being garbage collected, which can leak the memory
 * permanently if the reference is held in the singleton scope.
 *
 * To avoid this, use a non-nested class which is not an activity nor fragment. Hold a weak
 * reference to the activity or fragment and use that when making calls affecting the UI.
 */
@SuppressWarnings({"AndroidApiChecker"})
public class ModelLoader {
    private static final String TAG = "ModelLoader";
    private final WeakReference<ModelLoaderCallbacks> owner;
    private CompletableFuture<ModelRenderable> future;

    ModelLoader(ModelLoaderCallbacks owner) {
        this.owner = new WeakReference<>(owner);
    }

    /**
     * Starts loading the model specified. The result of the loading is returned asynchrounously via
     * {@link ModelLoaderCallbacks#setRenderable(ModelRenderable)} or {@link
     * ModelLoaderCallbacks#onLoadException(Throwable)} (Throwable)}.
     *
     * takes in a resource string to create dynamic model at runtime
     *
     *
     * @return true if loading was initiated.
     */
    boolean loadModel(Context context, Uri resourceId) {

        future =
                ModelRenderable.builder()
                        .setSource(context, RenderableSource.builder().setSource(
                                context,
                                resourceId,
                                RenderableSource.SourceType.GLTF2).build())
                        .build()
                        .thenApply(this::setRenderable)
                        .exceptionally(this::onException);
        return future != null;
    }

    ModelRenderable onException(Throwable throwable) {
        ModelLoaderCallbacks listener = owner.get();
        if (listener != null) {
            listener.onLoadException(throwable);
        }
        return null;
    }

    ModelRenderable setRenderable(ModelRenderable modelRenderable) {
        ModelLoaderCallbacks listener = owner.get();
        if (listener != null) {
            listener.setRenderable(modelRenderable);
        }
        return modelRenderable;
    }

    /** Callbacks for handling the loading results. */
    public interface ModelLoaderCallbacks {
        void setRenderable(ModelRenderable modelRenderable);

        void onLoadException(Throwable throwable);
    }
}
