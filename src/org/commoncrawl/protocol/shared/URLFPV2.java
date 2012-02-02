// File generated by rpc compiler. Do not edit.

package org.commoncrawl.protocol.shared;

import java.io.DataInput;
import java.io.DataOutput;
import java.util.BitSet;
import java.io.IOException;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.record.Buffer;
import org.commoncrawl.util.shared.FlexBuffer;
import org.commoncrawl.util.shared.TextBytes;
import org.commoncrawl.util.shared.MurmurHash;
import org.commoncrawl.util.shared.ImmutableBuffer;
import org.commoncrawl.rpc.BinaryProtocol;
import org.apache.hadoop.util.ReflectionUtils;
import org.apache.hadoop.conf.Configuration;
// Generated File: URLFPV2
public class URLFPV2 extends org.commoncrawl.rpc.RPCStruct<URLFPV2>  implements WritableComparable {
  
  // optimized constructor helper 
  public static URLFPV2 newInstance(Configuration conf) {
      return ReflectionUtils.newInstance(URLFPV2.class,conf);
  }
  // Writable Implementation
  public void write(DataOutput out) throws IOException{ 
    this.serialize(out,new BinaryProtocol());
  }
  
  public void readFields(DataInput  in) throws IOException{ 
    this.deserialize(in,new BinaryProtocol());
  }
  
  // Comparable Implementation
  public int compareTo(Object other) {
    int result = ((Long)domainHash).compareTo((Long)((org.commoncrawl.protocol.shared.URLFPV2)other).domainHash);
    if (result == 0) result = ((Long)urlHash).compareTo((Long)((org.commoncrawl.protocol.shared.URLFPV2)other).urlHash);
    return result;
  }
  // getKey Implementation
  public String getKey(){
     return"domainHash_"+((Long)domainHash).toString()+ "_" +"urlHash_"+((Long)urlHash).toString();
  }
  
  
  // Field Constants
  public static final int Field_DOMAINHASH = 1;
  public static final int Field_URLHASH = 2;
  public static final int Field_ROOTDOMAINHASH = 3;
  static final int FieldID_MAX=Field_ROOTDOMAINHASH;
  
  // Field Declarations
  private long domainHash;
  private long urlHash;
  private long rootDomainHash;
  
  // Default Constructor
  public URLFPV2() { }
  
  // Accessors
  
  public final boolean isFieldDirty(int fieldId) { return true; }
  public final URLFPV2 setFieldDirty(int fieldId) { return this; }
  
  public final URLFPV2 setFieldClean(int fieldId) { return this; }
  
  public long getDomainHash() {
    return domainHash;
  }
  public URLFPV2 setDomainHash( long domainHash) {
    this.domainHash=domainHash;
    return this;
  }
  public long getUrlHash() {
    return urlHash;
  }
  public URLFPV2 setUrlHash( long urlHash) {
    this.urlHash=urlHash;
    return this;
  }
  public long getRootDomainHash() {
    return rootDomainHash;
  }
  public URLFPV2 setRootDomainHash( long rootDomainHash) {
    this.rootDomainHash=rootDomainHash;
    return this;
  }
  // Object Dirty support 
  
  public final boolean isObjectDirty(){ return true; } 
  
  // serialize implementation 
  public final void serialize(DataOutput output,BinaryProtocol encoder)
  throws java.io.IOException {
    encoder.beginFields(output);
    // serialize field:domainHash
    {
      encoder.beginField(output,"domainHash",Field_DOMAINHASH);
      encoder.writeVLong(output,domainHash);
    }
    // serialize field:urlHash
    {
      encoder.beginField(output,"urlHash",Field_URLHASH);
      encoder.writeVLong(output,urlHash);
    }
    // serialize field:rootDomainHash
    {
      encoder.beginField(output,"rootDomainHash",Field_ROOTDOMAINHASH);
      encoder.writeVLong(output,rootDomainHash);
    }
    encoder.endFields(output);
  }
  // deserialize implementation 
  public final void deserialize(DataInput input, BinaryProtocol decoder)
  throws java.io.IOException {
    // clear existing data first  
    clear();
    
    // reset protocol object to unknown field id enconding mode (for compatibility)
    decoder.pushFieldIdEncodingMode(BinaryProtocol.FIELD_ID_ENCODING_MODE_UNKNOWN);
    // keep reading fields until terminator (-1) is located 
    int fieldId;
    while ((fieldId = decoder.readFieldId(input)) != -1) { 
      switch (fieldId) { 
        case Field_DOMAINHASH:{
          domainHash=decoder.readVLong(input);
        }
        break;
        case Field_URLHASH:{
          urlHash=decoder.readVLong(input);
        }
        break;
        case Field_ROOTDOMAINHASH:{
          rootDomainHash=decoder.readVLong(input);
        }
        break;
      }
    }
    // pop extra encoding mode off of stack 
    decoder.popFieldIdEncodingMode();
  }
  // clear implementation 
  public final void clear() {
    domainHash=0;
    urlHash=0;
    rootDomainHash=0;
  }
  // equals implementation 
  public final boolean equals(final Object peer_) {
    if (!(peer_ instanceof URLFPV2)) {
      return false;
    }
    if (peer_ == this) {
      return true;
    }
    URLFPV2 peer = (URLFPV2) peer_;
    boolean ret = true;
     {
      ret = (domainHash==peer.domainHash);
      if (!ret) return ret;
    }
     {
      ret = (urlHash==peer.urlHash);
      if (!ret) return ret;
    }
     {
      ret = (rootDomainHash==peer.rootDomainHash);
      if (!ret) return ret;
    }
    return ret;
  }
  // clone implementation 
  @SuppressWarnings("unchecked")
  public final Object clone() throws CloneNotSupportedException {
    URLFPV2 other = new URLFPV2();
    {
      other.domainHash= this.domainHash;
    }
    {
      other.urlHash= this.urlHash;
    }
    {
      other.rootDomainHash= this.rootDomainHash;
    }
    return other;
  }
  // merge implementation 
  @SuppressWarnings("unchecked")
  public final void merge(URLFPV2 peer) throws CloneNotSupportedException  {
    {
      this.domainHash= peer.domainHash;
    }
    {
      this.urlHash= peer.urlHash;
    }
    {
      this.rootDomainHash= peer.rootDomainHash;
    }
  }
  // hashCode implementation 
  public final int hashCode() {
    int result = 1;
    result = MurmurHash.hashLong(domainHash,result);
    result = MurmurHash.hashLong(urlHash,result);
    return result;
  }
}