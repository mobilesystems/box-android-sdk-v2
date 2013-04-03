package com.box.boxandroidlibv2.jacksonparser;

import com.box.boxandroidlibv2.dao.BoxAndroidCollaboration;
import com.box.boxandroidlibv2.dao.BoxAndroidCollection;
import com.box.boxandroidlibv2.dao.BoxAndroidComment;
import com.box.boxandroidlibv2.dao.BoxAndroidEmailAlias;
import com.box.boxandroidlibv2.dao.BoxAndroidFile;
import com.box.boxandroidlibv2.dao.BoxAndroidFileVersion;
import com.box.boxandroidlibv2.dao.BoxAndroidFolder;
import com.box.boxandroidlibv2.dao.BoxAndroidOAuthData;
import com.box.boxjavalibv2.dao.BoxItem;
import com.box.boxjavalibv2.dao.BoxPreview;
import com.box.boxjavalibv2.dao.BoxResourceType;
import com.box.boxjavalibv2.dao.BoxTypedObject;
import com.box.boxjavalibv2.dao.BoxUser;
import com.box.boxjavalibv2.interfaces.IBoxResourceHub;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Resource hub to direct the parsing of the api responses into our android sdk data objects.
 */
public class AndroidBoxResourceHub implements IBoxResourceHub {

    private final ObjectMapper mObjectMapper;

    public AndroidBoxResourceHub() {
        mObjectMapper = new ObjectMapper();
        mObjectMapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
        mObjectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mObjectMapper.addMixInAnnotations(BoxItem.class, AndroidBoxItemMixIn.class);
        mObjectMapper.addMixInAnnotations(BoxTypedObject.class, AndroidBoxTypedObjectMixIn.class);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Class getClass(BoxResourceType type) {
        switch (type) {
            case FILE:
                return BoxAndroidFile.class;
            case PREVIEW:
                return BoxPreview.class;
            case FOLDER:
                return BoxAndroidFolder.class;
            case USER:
                return BoxUser.class;
            case FILE_VERSION:
                return BoxAndroidFileVersion.class;
            case ITEM:
                return BoxItem.class;
            case COMMENT:
                return BoxAndroidComment.class;
            case COLLABORATION:
                return BoxAndroidCollaboration.class;
            case EMAIL_ALIAS:
                return BoxAndroidEmailAlias.class;
            case OAUTH_DATA:
                return BoxAndroidOAuthData.class;
            case ITEMS:
            case FILES:
            case USERS:
            case COMMENTS:
            case FILE_VERSIONS:
            case COLLABORATIONS:
            case EMAIL_ALIASES:
                return BoxAndroidCollection.class;
            default:
                return BoxTypedObject.class;
        }
    }

    @Override
    public ObjectMapper getObjectMapper() {
        return mObjectMapper;
    }

}
