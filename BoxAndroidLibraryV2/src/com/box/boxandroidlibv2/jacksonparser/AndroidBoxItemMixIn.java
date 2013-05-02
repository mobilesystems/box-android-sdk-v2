package com.box.boxandroidlibv2.jacksonparser;

import com.box.boxandroidlibv2.dao.BoxAndroidEvent;
import com.box.boxandroidlibv2.dao.BoxAndroidFile;
import com.box.boxandroidlibv2.dao.BoxAndroidFolder;
import com.box.boxandroidlibv2.dao.BoxAndroidWebLink;
import com.box.boxjavalibv2.dao.BoxRealTimeServer;
import com.box.boxjavalibv2.interfaces.IJacksonMixIn;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * A jackson <a href="http://wiki.fasterxml.com/JacksonMixInAnnotations">MixIn</a> class to direct the parsing of BoxItem types into proper sub type.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({@Type(value = BoxAndroidFile.class, name = "file"), @Type(value = BoxAndroidFolder.class, name = "folder"),
               @Type(value = BoxAndroidWebLink.class, name = "web_link"), @Type(value = BoxAndroidEvent.class, name = "event"),
               @Type(value = BoxRealTimeServer.class, name = "realtime_server")})
public class AndroidBoxItemMixIn implements IJacksonMixIn {

}
