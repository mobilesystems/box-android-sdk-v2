package com.box.boxandroidlibv2.jacksonparser;

import com.box.boxandroidlibv2.dao.BoxAndroidCollaboration;
import com.box.boxandroidlibv2.dao.BoxAndroidComment;
import com.box.boxandroidlibv2.dao.BoxAndroidEmailAlias;
import com.box.boxandroidlibv2.dao.BoxAndroidEvent;
import com.box.boxandroidlibv2.dao.BoxAndroidFile;
import com.box.boxandroidlibv2.dao.BoxAndroidFileVersion;
import com.box.boxandroidlibv2.dao.BoxAndroidFolder;
import com.box.boxandroidlibv2.dao.BoxAndroidUser;
import com.box.boxjavalibv2.dao.BoxRealTimeServer;
import com.box.boxjavalibv2.dao.BoxServerError;
import com.box.boxjavalibv2.dao.BoxWebLink;
import com.box.boxjavalibv2.interfaces.IJacksonMixIn;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * A jackson <a href="http://wiki.fasterxml.com/JacksonMixInAnnotations">MixIn</a> class to direct the parsing of BoxTypedObject type into proper sub type.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({@Type(value = BoxAndroidFile.class, name = "file"), @Type(value = BoxAndroidFolder.class, name = "folder"),
               @Type(value = BoxWebLink.class, name = "web_link"), @Type(value = BoxAndroidCollaboration.class, name = "collaboration"),
               @Type(value = BoxAndroidComment.class, name = "comment"), @Type(value = BoxAndroidEmailAlias.class, name = "email_alias"),
               @Type(value = BoxAndroidFileVersion.class, name = "file_version"), @Type(value = BoxAndroidUser.class, name = "user"),
               @Type(value = BoxServerError.class, name = "error"), @Type(value = BoxAndroidEvent.class, name = "event"),
               @Type(value = BoxRealTimeServer.class, name = "realtime_server")})
public class AndroidBoxTypedObjectMixIn implements IJacksonMixIn {

}
