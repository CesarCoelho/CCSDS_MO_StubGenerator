/* ----------------------------------------------------------------------------
 * Copyright (C) 2013      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : CCSDS MO Service Stub Generator
 * ----------------------------------------------------------------------------
 * Licensed under the European Space Agency Public License, Version 2.0
 * You may not use this file except in compliance with the License.
 *
 * Except as expressly set forth in this License, the Software is provided to
 * You on an "as is" basis and without warranties of any kind, including without
 * limitation merchantability, fitness for a particular purpose, absence of
 * defects or errors, accuracy or non-infringement of intellectual property rights.
 * 
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 * ----------------------------------------------------------------------------
 */
package esa.mo.tools.stubgen;

import esa.mo.tools.stubgen.specification.AttributeTypeDetails;
import esa.mo.tools.stubgen.specification.CompositeField;
import esa.mo.tools.stubgen.specification.NativeTypeDetails;
import esa.mo.tools.stubgen.specification.OperationSummary;
import esa.mo.tools.stubgen.specification.ServiceSummary;
import esa.mo.tools.stubgen.specification.StdStrings;
import esa.mo.tools.stubgen.specification.TypeInfo;
import esa.mo.tools.stubgen.specification.TypeUtils;
import esa.mo.tools.stubgen.writers.AbstractLanguageWriter;
import esa.mo.tools.stubgen.writers.ClassWriter;
import esa.mo.tools.stubgen.writers.InterfaceWriter;
import esa.mo.tools.stubgen.writers.LanguageWriter;
import esa.mo.tools.stubgen.writers.MethodWriter;
import esa.mo.tools.stubgen.writers.TargetWriter;
import esa.mo.tools.stubgen.xsd.AreaType;
import esa.mo.tools.stubgen.xsd.EnumerationType;
import esa.mo.tools.stubgen.xsd.ServiceType;
import esa.mo.tools.stubgen.xsd.TypeReference;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Generates stubs and skeletons for CCSDS MO Service specifications for the Java language.
 */
public class GeneratorJava extends GeneratorLangs
{
  /**
   * The file extension for Java files.
   */
  public static final String JAVA_FILE_EXT = "java";
  /**
   * The file name for package level comments.
   */
  public static final String JAVA_PACKAGE_COMMENT_FILE_NAME = "package-info";

  /**
   * Constructor.
   *
   * @param logger The logger to use.
   */
  public GeneratorJava(org.apache.maven.plugin.logging.Log logger)
  {
    super(logger, true, true, false, true, false, "/org/ccsds/moims/mo", "register", "deregister",
            new GeneratorConfiguration("org.ccsds.moims.mo.", "structures", "structures.factory", "body", ".", "(Object[]) null",
            "org.ccsds.moims.mo.mal.MALSendOperation",
            "org.ccsds.moims.mo.mal.MALSubmitOperation",
            "org.ccsds.moims.mo.mal.MALRequestOperation",
            "org.ccsds.moims.mo.mal.MALInvokeOperation",
            "org.ccsds.moims.mo.mal.MALProgressOperation",
            "org.ccsds.moims.mo.mal.MALPubSubOperation"));
  }

  @Override
  public String getShortName()
  {
    return "Java";
  }

  @Override
  public String getDescription()
  {
    return "Generates a Java language mapping.";
  }

  @Override
  public void init(String destinationFolderName, boolean generateStructures, boolean generateCOM, Map<String, String> extraProperties) throws IOException
  {
    super.init(destinationFolderName, generateStructures, generateCOM, extraProperties);

    setRequiresDefaultConstructors(Boolean.valueOf(extraProperties.get("requiresDefaultConstructors")));

    addAttributeType(StdStrings.BLOB, new AttributeTypeDetails(this, false, "Blob", ""));
    addAttributeType(StdStrings.BOOLEAN, new AttributeTypeDetails(this, true, "Boolean", "Boolean.FALSE"));
    addAttributeType(StdStrings.DOUBLE, new AttributeTypeDetails(this, true, "Double", "Double.MAX_VALUE"));
    addAttributeType(StdStrings.DURATION, new AttributeTypeDetails(this, false, "Duration", ""));
    addAttributeType(StdStrings.FLOAT, new AttributeTypeDetails(this, true, "Float", "Float.MAX_VALUE"));
    addAttributeType(StdStrings.INTEGER, new AttributeTypeDetails(this, true, "Integer", "Integer.MAX_VALUE"));
    addAttributeType(StdStrings.IDENTIFIER, new AttributeTypeDetails(this, false, "Identifier", ""));
    addAttributeType(StdStrings.LONG, new AttributeTypeDetails(this, true, "Long", "Long.MAX_VALUE"));
    addAttributeType(StdStrings.OCTET, new AttributeTypeDetails(this, true, "Byte", "Byte.MAX_VALUE"));
    addAttributeType(StdStrings.SHORT, new AttributeTypeDetails(this, true, "Short", "Short.MAX_VALUE"));
    addAttributeType(StdStrings.UINTEGER, new AttributeTypeDetails(this, false, "UInteger", ""));
    addAttributeType(StdStrings.ULONG, new AttributeTypeDetails(this, false, "ULong", ""));
    addAttributeType(StdStrings.UOCTET, new AttributeTypeDetails(this, false, "UOctet", ""));
    addAttributeType(StdStrings.USHORT, new AttributeTypeDetails(this, false, "UShort", ""));
    addAttributeType(StdStrings.STRING, new AttributeTypeDetails(this, true, "String", "\"\""));
    addAttributeType(StdStrings.TIME, new AttributeTypeDetails(this, false, "Time", ""));
    addAttributeType(StdStrings.FINETIME, new AttributeTypeDetails(this, false, "FineTime", ""));
    addAttributeType(StdStrings.URI, new AttributeTypeDetails(this, false, "URI", ""));

    addNativeType("boolean", new NativeTypeDetails("boolean", false, false, null));
    addNativeType("_String", new NativeTypeDetails("String", false, false, null));
    addNativeType("byte", new NativeTypeDetails("byte", false, false, null));
    addNativeType("short", new NativeTypeDetails("short", false, false, null));
    addNativeType("int", new NativeTypeDetails("int", false, false, null));
    addNativeType("long", new NativeTypeDetails("long", false, false, null));
    addNativeType("_Integer", new NativeTypeDetails("Integer", true, false, null));
    addNativeType("Class", new NativeTypeDetails("Class", true, false, null));
    addNativeType("Map", new NativeTypeDetails("java.util.Map", true, false, null));
    addNativeType("Vector", new NativeTypeDetails("java.util.Vector", true, false, null));
  }

  @Override
  public void createRequiredPublisher(String destinationFolderName, String fqPublisherName, OperationSummary op) throws IOException
  {
    getLog().info("Creating publisher class " + fqPublisherName);

    String publisherPackage = fqPublisherName.substring(0, fqPublisherName.lastIndexOf('.'));
    String publisherName = fqPublisherName.substring(fqPublisherName.lastIndexOf('.') + 1);
    ClassWriter file = createClassFile(destinationFolderName, fqPublisherName.replace('.', '/'));

    file.addPackageStatement(publisherPackage.toLowerCase(), "");

    String throwsMALException = createElementType(file, StdStrings.MAL, null, null, StdStrings.MALEXCEPTION);
    String throwsInteractionException = createElementType(file, StdStrings.MAL, null, null, StdStrings.MALINTERACTIONEXCEPTION);
    String throwsInteractionAndMALException = throwsInteractionException + ", " + throwsMALException;
    String throwsExceptions = "java.lang.IllegalArgumentException, " + throwsInteractionAndMALException;
    String publisherSetType = createElementType(file, StdStrings.MAL, null, PROVIDER_FOLDER, "MALPublisherSet");

    file.addClassOpenStatement(publisherName, true, false, null, null, "Publisher class for the " + op.getName() + " operation.");

    file.addClassVariable(false, false, StdStrings.PRIVATE, publisherSetType, false, false, false, "publisherSet", (String) null, null);

    MethodWriter method = file.addConstructor(StdStrings.PUBLIC, publisherName, publisherSetType + " publisherSet", "", null, "Creates an instance of this class using the supplied publisher set.", Arrays.asList("publisherSet The set of broker connections to use when registering and publishing."), null);
    method.addMethodStatement("this.publisherSet = publisherSet");
    method.addMethodCloseStatement();

    method = file.addMethodOpenStatement(false, false, StdStrings.PUBLIC, false, true, StdStrings.VOID, "register", "org.ccsds.moims.mo.mal.structures.EntityKeyList entityKeys, org.ccsds.moims.mo.mal.provider.MALPublishInteractionListener listener", throwsExceptions,
            "Registers this provider implementation to the set of broker connections", null, Arrays.asList("entityKeys The entity keys to use in the method", "listener The listener object to use for callback from the publisher"), Arrays.asList("java.lang.IllegalArgumentException If any supplied argument is invalid", throwsInteractionException + " if there is a problem during the interaction as defined by the MAL specification.", throwsMALException + " if there is an implementation exception"));
    method.addMethodStatement("publisherSet.register(entityKeys, listener)");
    method.addMethodCloseStatement();

    method = file.addMethodOpenStatement(false, false, StdStrings.PUBLIC, false, true, StdStrings.VOID, "asyncRegister", "org.ccsds.moims.mo.mal.structures.EntityKeyList entityKeys, org.ccsds.moims.mo.mal.provider.MALPublishInteractionListener listener", throwsExceptions,
            "Asynchronously registers this provider implementation to the set of broker connections", null, Arrays.asList("entityKeys The entity keys to use in the method", "listener The listener object to use for callback from the publisher"), Arrays.asList("java.lang.IllegalArgumentException If any supplied argument is invalid", throwsInteractionException + " if there is a problem during the interaction as defined by the MAL specification.", throwsMALException + " if there is an implementation exception"));
    method.addMethodStatement("publisherSet.asyncRegister(entityKeys, listener)");
    method.addMethodCloseStatement();

    List<String> opArgComments = new LinkedList<String>();
    opArgComments.add("updateHeaderList The headers of the updates being added");
    String argList = createRequiredPublisherArgs(op.getUpdateTypes(), true, opArgComments);
    String argNameList = createRequiredPublisherArgs(op.getUpdateTypes(), false, null);
    method = file.addMethodOpenStatement(false, false, StdStrings.PUBLIC, false, true, StdStrings.VOID, "publish", "org.ccsds.moims.mo.mal.structures.UpdateHeaderList updateHeaderList" + argList, throwsExceptions,
            "Publishes updates to the set of registered broker connections", null, opArgComments, Arrays.asList("java.lang.IllegalArgumentException If any supplied argument is invalid", throwsInteractionException + " if there is a problem during the interaction as defined by the MAL specification.", throwsMALException + " if there is an implementation exception"));
    method.addMethodStatement("publisherSet.publish(updateHeaderList" + argNameList + ")");
    method.addMethodCloseStatement();

    method = file.addMethodOpenStatement(false, false, StdStrings.PUBLIC, false, true, StdStrings.VOID, "deregister", "", throwsInteractionAndMALException,
            "Deregisters this provider implementation from the set of broker connections", null, null, Arrays.asList(throwsInteractionException + " if there is a problem during the interaction as defined by the MAL specification.", throwsMALException + " if there is an implementation exception"));
    method.addMethodStatement("publisherSet.deregister()");
    method.addMethodCloseStatement();

    method = file.addMethodOpenStatement(false, false, StdStrings.PUBLIC, false, true, StdStrings.VOID, "asyncDeregister", "org.ccsds.moims.mo.mal.provider.MALPublishInteractionListener listener", throwsExceptions,
            "Asynchronously deregisters this provider implementation from the set of broker connections", null, Arrays.asList("listener The listener object to use for callback from the publisher"), Arrays.asList("java.lang.IllegalArgumentException If any supplied argument is invalid", throwsInteractionException + " if there is a problem during the interaction as defined by the MAL specification.", throwsMALException + " if there is an implementation exception"));
    method.addMethodStatement("publisherSet.asyncDeregister(listener)");
    method.addMethodCloseStatement();

    method = file.addMethodOpenStatement(false, false, StdStrings.PUBLIC, false, true, StdStrings.VOID, "close", "", throwsMALException,
            "Closes this publisher", null, null, Arrays.asList(throwsMALException + " if there is an implementation exception"));
    method.addMethodStatement("publisherSet.close()");
    method.addMethodCloseStatement();

    file.addClassCloseStatement();

    file.flush();
  }

  @Override
  protected void createListClass(File folder, AreaType area, ServiceType service, String srcTypeName, boolean isAbstract, Long shortFormPart) throws IOException
  {
    if (isAbstract)
    {
      createAbstractListClass(folder, area, service, srcTypeName);
    }
    else
    {
      createConcreteListClass(folder, area, service, srcTypeName, shortFormPart);
    }
  }

  /**
   * Creates a list for an abstract type.
   *
   * @param folder The base folder to create the list in.
   * @param area The Area of the list.
   * @param service The service of the list.
   * @param srcTypeName The name of the element in the list.
   * @throws IOException if there is a problem writing the file.
   */
  protected void createAbstractListClass(File folder, AreaType area, ServiceType service, String srcTypeName) throws IOException
  {
    String listName = srcTypeName + "List";

    getLog().info("Creating list interface " + listName);

    InterfaceWriter file = createInterfaceFile(folder, listName);

    file.addPackageStatement(area, service, getConfig().getStructureFolder());

    TypeReference superTypeReference = getCompositeElementSuperType(srcTypeName);
    String fqSrcTypeName = createElementType(file, area, service, srcTypeName);

    if (null == superTypeReference)
    {
      superTypeReference = new TypeReference();
      superTypeReference.setArea(StdStrings.MAL);
      superTypeReference.setName(StdStrings.ELEMENT);
    }

    CompositeField listSuperElement = createCompositeElementsDetails(file, null, superTypeReference, true, "List element.");

    file.addInterfaceOpenStatement(listName + "<T extends " + fqSrcTypeName + ">", listSuperElement.getTypeName() + "List<T>", "List class for " + srcTypeName + "." + file.getLineSeparator() + " * @param <T> The type of this list must extend " + srcTypeName);
    file.addInterfaceCloseStatement();

    file.flush();
  }

  /**
   * Creates a list for an abstract type.
   *
   * @param folder The base folder to create the list in.
   * @param area The Area of the list.
   * @param service The service of the list.
   * @param srcTypeName The name of the element in the list.
   * @param shortFormPart The short form part of the contained element.
   * @throws IOException if there is a problem writing the file.
   */
  protected void createConcreteListClass(File folder, AreaType area, ServiceType service, String srcTypeName, Long shortFormPart) throws IOException
  {
    String listName = srcTypeName + "List";

    TypeReference srcType = new TypeReference();
    srcType.setArea(area.getName());
    if (null != service)
    {
      srcType.setService(service.getName());
    }
    srcType.setName(srcTypeName);

    getLog().info("Creating list class " + listName);

    ClassWriter file = createClassFile(folder, listName);

    file.addPackageStatement(area, service, getConfig().getStructureFolder());

    String elementType = createElementType(file, StdStrings.MAL, null, StdStrings.ELEMENT);
    String throwsMALException = createElementType(file, StdStrings.MAL, null, null, StdStrings.MALEXCEPTION);
    String fqSrcTypeName = createElementType(file, area, service, srcTypeName);

    TypeReference superTypeReference = getCompositeElementSuperType(srcTypeName);
    if (null == superTypeReference)
    {
      superTypeReference = new TypeReference();
      superTypeReference.setArea(StdStrings.MAL);
      if (isAttributeType(srcType))
      {
        superTypeReference.setName(StdStrings.ATTRIBUTE);
      }
      else
      {
        if (isEnum(srcType))
        {
          superTypeReference.setName(StdStrings.ENUMERATION);
        }
        else
        {
          superTypeReference.setName(StdStrings.COMPOSITE);
        }
      }
    }

    CompositeField listSuperElement = createCompositeElementsDetails(file, null, superTypeReference, true, "List element.");

    file.addClassOpenStatement(listName, true, false, "java.util.ArrayList<" + fqSrcTypeName + ">", listSuperElement.getTypeName() + "List<" + fqSrcTypeName + ">", "List class for " + srcTypeName + ".");

    CompositeField listElement = createCompositeElementsDetails(file, null, srcType, true, "List element.");

    addTypeShortForm(file, -shortFormPart);
    file.addClassVariable(true, true, StdStrings.PUBLIC, StdStrings.USHORT, true, false, false, "AREA_SHORT_FORM", "(" + area.getNumber() + ")", "Short form for area.");
    file.addClassVariable(true, true, StdStrings.PUBLIC, StdStrings.UOCTET, true, false, false, "AREA_VERSION", "((short)" + area.getVersion() + ")", "Version for area.");
    StringBuilder asf = new StringBuilder();
    asf.append(String.format("%04X", area.getNumber()));
    if (null != service)
    {
      file.addClassVariable(true, true, StdStrings.PUBLIC, StdStrings.USHORT, true, false, false, "SERVICE_SHORT_FORM", "(" + service.getNumber() + ")", "Short form for service.");
      asf.append(String.format("%04X", service.getNumber()));
    }
    else
    {
      file.addClassVariable(true, true, StdStrings.PUBLIC, StdStrings.USHORT, true, false, false, "SERVICE_SHORT_FORM", "(0)", "Short form for service.");
      asf.append("0000");
    }

    asf.append(String.format("%02X", area.getVersion()));
    asf.append(Integer.toHexString(-shortFormPart.intValue()).toUpperCase().substring(2));
    addShortForm(file, Long.parseLong(asf.toString(), 16));

    // create blank constructor
    file.addConstructorDefault(listName);

    // create initial size contructor
    MethodWriter method = file.addConstructor(StdStrings.PUBLIC, listName, "int initialCapacity", "initialCapacity", null, "Constructor that initialises the capacity of the list.", Arrays.asList("initialCapacity the required initial capacity."), null);
    method.addMethodCloseStatement();

    method = file.addMethodOpenStatement(true, false, StdStrings.PUBLIC, false, true, elementType, "createElement", "", null, "Creates an instance of this type using the default constructor. It is a generic factory method.", "A new instance of this type with default field values.", null, null);
    method.addMethodStatement("return new " + listName + "()");
    method.addMethodCloseStatement();

    // create encode method
    method = file.addMethodOpenStatement(false, false, StdStrings.PUBLIC, false, true, StdStrings.VOID, "encode", "org.ccsds.moims.mo.mal.MALEncoder encoder", throwsMALException, "Encodes the value of this object using the provided MALEncoder.", null, Arrays.asList("encoder - the encoder to use for encoding"), Arrays.asList(throwsMALException + " if any encoding errors are detected."));
    method.addMethodStatement("org.ccsds.moims.mo.mal.MALListEncoder listEncoder = encoder.createListEncoder(this)");
    method.addMethodStatement("for (int i = 0; i < size(); i++)", false);
    method.addMethodStatement("{", false);
    method.addMethodStatement("  listEncoder.encodeNullable" + listElement.getEncodeCall() + "((" + fqSrcTypeName + ") get(i))");
    method.addMethodStatement("}", false);
    method.addMethodStatement("listEncoder.close()");
    method.addMethodCloseStatement();

    // create decode method
    method = file.addMethodOpenStatement(false, false, StdStrings.PUBLIC, false, true, elementType, "decode", "org.ccsds.moims.mo.mal.MALDecoder decoder", throwsMALException, "Decodes the value of this object using the provided MALDecoder.", "Returns this object.", Arrays.asList("decoder - the decoder to use for decoding"), Arrays.asList(throwsMALException + " if any decoding errors are detected."));
    method.addMethodStatement("org.ccsds.moims.mo.mal.MALListDecoder listDecoder = decoder.createListDecoder(this)");
    method.addMethodStatement("while (listDecoder.hasNext())", false);
    method.addMethodStatement("{", false);
    method.addMethodStatement("  add(" + listElement.getDecodeCast() + "listDecoder.decodeNullable" + listElement.getDecodeCall() + "(" + (listElement.isDecodeNeedsNewCall() ? listElement.getNewCall() : "") + "))");
    method.addMethodStatement("}", false);
    method.addMethodStatement("return this");
    method.addMethodCloseStatement();

    addShortFormMethods(file);

    file.addClassCloseStatement();

    file.flush();

    createFactoryClass(folder, area, service, listName, false, false);
  }

  @Override
  protected void addTypeShortForm(ClassWriter file, long sf) throws IOException
  {
    file.addMultilineComment("Short form for type.");
    file.addStatement("  public static final Integer TYPE_SHORT_FORM = Integer.valueOf(" + sf + ");");
  }

  @Override
  protected void addShortForm(ClassWriter file, long sf) throws IOException
  {
    file.addMultilineComment("Absolute short form for type.");
    file.addStatement("  public static final Long SHORT_FORM = Long.valueOf(" + sf + "L);");
    file.addStatement("  private static final long serialVersionUID = " + sf + "L;");
  }

  @Override
  protected void createAreaFolderComment(File structureFolder, AreaType area) throws IOException
  {
    String cmt = area.getComment();
    if (null == cmt)
    {
      cmt = "The " + area.getName() + " area.";
    }

    createFolderComment(structureFolder, area, null, null, cmt);
  }

  @Override
  protected void createServiceFolderComment(File structureFolder, AreaType area, ServiceType service) throws IOException
  {
    createFolderComment(structureFolder, area, service, null, service.getComment());
  }

  @Override
  protected void createServiceConsumerFolderComment(File structureFolder, AreaType area, ServiceType service) throws IOException
  {
    createFolderComment(structureFolder, area, service, CONSUMER_FOLDER, "Package containing the consumer stubs for the " + service.getName() + " service.");
  }

  @Override
  protected void createServiceProviderFolderComment(File structureFolder, AreaType area, ServiceType service) throws IOException
  {
    createFolderComment(structureFolder, area, service, PROVIDER_FOLDER, "Package containing the provider skeletons for the " + service.getName() + " service.");
  }

  @Override
  protected void createServiceMessageBodyFolderComment(String baseFolder, String packageName) throws IOException
  {
    ClassWriter file = createClassFile(baseFolder, (packageName + "." + JAVA_PACKAGE_COMMENT_FILE_NAME).replace('.', '/'));

    createFolderComment(file, null, null, packageName.substring(getConfig().getBasePackage().length()), "Package containing the types for holding compound messages.");
  }

  @Override
  protected void createAreaStructureFolderComment(File structureFolder, AreaType area) throws IOException
  {
    createFolderComment(structureFolder, area, null, getConfig().getStructureFolder(), "Package containing types defined in the " + area.getName() + " area.");
  }

  @Override
  protected void createServiceStructureFolderComment(File structureFolder, AreaType area, ServiceType service) throws IOException
  {
    createFolderComment(structureFolder, area, service, getConfig().getStructureFolder(), "Package containing types defined in the " + service.getName() + " service.");
  }

  @Override
  protected void createStructureFactoryFolderComment(File structureFolder, AreaType area, ServiceType service) throws IOException
  {
    createFolderComment(structureFolder, area, service, getConfig().getFactoryFolder(), "Factory classes for the types defined in the "
            + ((null == service) ? (area.getName() + " area.") : (service.getName() + " service.")));
  }

  /**
   *
   * @param structureFolder
   * @param area
   * @param service
   * @param extraPackage
   * @param comment
   * @throws IOException
   */
  protected void createFolderComment(File structureFolder, AreaType area, ServiceType service, String extraPackage, String comment) throws IOException
  {
    ClassWriter file = createClassFile(structureFolder, JAVA_PACKAGE_COMMENT_FILE_NAME);

    createFolderComment(file, area, service, extraPackage, comment);
  }

  /**
   *
   * @param file
   * @param area
   * @param service
   * @param extraPackage
   * @param comment
   * @throws IOException
   */
  protected void createFolderComment(ClassWriter file, AreaType area, ServiceType service, String extraPackage, String comment) throws IOException
  {
    String packageName = "";

    if (null != area)
    {
      packageName += area.getName().toLowerCase();
    }
    if (null != service)
    {
      packageName += "." + service.getName().toLowerCase();
    }
    if (null != extraPackage)
    {
      if (0 < packageName.length())
      {
        packageName += ".";
      }
      packageName += extraPackage;
    }

    file.addStatement("/**");
    file.addStatement(comment);
    file.addStatement("*/");
    file.addPackageStatement(packageName);

    file.flush();
  }

  @Override
  protected CompositeField createCompositeElementsDetails(TargetWriter file, String fieldName, TypeReference elementType, boolean canBeNull, String comment)
  {
    CompositeField ele;

    String typeName = elementType.getName();

    if (elementType.isList())
    {
      if (StdStrings.XML.equals(elementType.getArea()))
      {
        throw new IllegalArgumentException("XML type of (" + elementType.getService() + ":" + elementType.getName() + ") with maxOccurrs <> 1 is not permitted");
      }
      else
      {
        String fqTypeName;
        if (isNativeType(elementType))
        {
          fqTypeName = createElementType((LanguageWriter) file, StdStrings.MAL, null, typeName + "List");
        }
        else
        {
          fqTypeName = createElementType((LanguageWriter) file, elementType) + "List";
        }

        String newCall = null;
        String encCall = null;
        if (!isAbstract(elementType))
        {
          newCall = "new " + fqTypeName + "()";
          encCall = StdStrings.ELEMENT;
        }

        ele = new CompositeField(fqTypeName, fieldName, elementType.isList(), canBeNull, encCall, "(" + fqTypeName + ") ", StdStrings.ELEMENT, true, newCall, comment);
      }
    }
    else
    {
      if (isAttributeType(elementType))
      {
        AttributeTypeDetails details = getAttributeDetails(elementType);
        String fqTypeName = createElementType((LanguageWriter) file, elementType);
        ele = new CompositeField(details.getTargetType(), fieldName, elementType.isList(), canBeNull, typeName, "", typeName, false, "new " + fqTypeName + "()", comment);
      }
      else
      {
        String fqTypeName = createElementType((LanguageWriter) file, elementType);
        if (isEnum(elementType))
        {
          EnumerationType typ = getEnum(typeName);
          String firstEle = fqTypeName + "." + typ.getItem().get(0).getValue();
          ele = new CompositeField(fqTypeName, fieldName, elementType.isList(), canBeNull, StdStrings.ELEMENT, "(" + fqTypeName + ") ", StdStrings.ELEMENT, true, firstEle, comment);
        }
        else if (StdStrings.ATTRIBUTE.equals(typeName))
        {
          ele = new CompositeField(fqTypeName, fieldName, elementType.isList(), canBeNull, StdStrings.ATTRIBUTE, "(" + fqTypeName + ") ", StdStrings.ATTRIBUTE, false, "", comment);
        }
        else if (StdStrings.ELEMENT.equals(typeName))
        {
          ele = new CompositeField(fqTypeName, fieldName, elementType.isList(), canBeNull, StdStrings.ELEMENT, "(" + fqTypeName + ") ", StdStrings.ELEMENT, false, "", comment);
        }
        else
        {
          ele = new CompositeField(fqTypeName, fieldName, elementType.isList(), canBeNull, StdStrings.ELEMENT, "(" + fqTypeName + ") ", StdStrings.ELEMENT, true, "new " + fqTypeName + "()", comment);
        }
      }
    }

    return ele;
  }

  @Override
  protected void addServiceConstructor(MethodWriter method, String serviceVar, String serviceVersion, ServiceSummary summary) throws IOException
  {
    String opCall = serviceVar + "_SERVICE.addOperation(";
    for (OperationSummary op : summary.getOperations())
    {
      method.addMethodStatement(opCall + op.getName().toUpperCase() + "_OP)");
    }
  }

  @Override
  protected String createAreaHelperClassInitialValue(String areaVar, short areaVersion)
  {
    return "(" + areaVar + "_AREA_NUMBER, " + areaVar + "_AREA_NAME, new org.ccsds.moims.mo.mal.structures.UOctet((short) " + areaVersion + "))";
  }

  @Override
  protected String createServiceHelperClassInitialValue(String serviceVar)
  {
    return "(" + serviceVar + "_SERVICE_NUMBER, " + serviceVar + "_SERVICE_NAME)";
  }

  @Override
  protected String getIntCallMethod()
  {
    return "intValue";
  }

  @Override
  protected String getOctetCallMethod()
  {
    return "byteValue";
  }

  @Override
  protected void addVectorAddStatement(LanguageWriter file, MethodWriter method, String variable, String parameter) throws IOException
  {
    method.addMethodStatement(variable + ".addElement(" + parameter + ")");
  }

  @Override
  protected void addVectorRemoveStatement(LanguageWriter file, MethodWriter method, String variable, String parameter) throws IOException
  {
    method.addMethodStatement(variable + ".removeElement(" + parameter + ")");
  }

  @Override
  protected String createStaticClassReference(String type)
  {
    return type + ".class";
  }

  @Override
  protected String addressOf(String type)
  {
    return type;
  }

  @Override
  protected String createArraySize(boolean isActual, String type, String variable)
  {
    return variable + ".length";
  }

  @Override
  protected String malStringAsElement(LanguageWriter file)
  {
    return createElementType(file, StdStrings.MAL, null, StdStrings.UNION);
  }

  @Override
  protected String errorCodeAsReference(LanguageWriter file, String ref)
  {
    return ref;
  }

  @Override
  protected ClassWriter createClassFile(File folder, String className) throws IOException
  {
    return new JavaClassWriter(folder, className);
  }

  @Override
  protected ClassWriter createClassFile(String destinationFolderName, String className) throws IOException
  {
    return new JavaClassWriter(destinationFolderName, className);
  }

  @Override
  protected InterfaceWriter createInterfaceFile(File folder, String className) throws IOException
  {
    return new JavaClassWriter(folder, className);
  }

  @Override
  protected InterfaceWriter createInterfaceFile(String destinationFolderName, String className) throws IOException
  {
    return new JavaClassWriter(destinationFolderName, className);
  }

  private String createRequiredPublisherArgs(List<TypeInfo> targetTypes, boolean withType, List<String> opArgComments) throws IOException
  {
    StringBuilder buf = new StringBuilder();

    if ((null != targetTypes) && (0 < targetTypes.size()))
    {
      for (int i = 0; i < targetTypes.size(); i++)
      {
        TypeInfo ti = targetTypes.get(i);

        buf.append(", ");
        if (withType)
        {
          if (isNativeType(ti.getSourceType()))
          {
            TypeReference tr = ti.getSourceType();
            tr.setList(true);
            TypeInfo lti = TypeUtils.convertTypeReference(this, tr);
            buf.append(lti.getTargetType());
            tr.setList(false);
            buf.append(" ");
          }
          else
          {
            buf.append(ti.getTargetType());
            buf.append("List ");
          }
        }
        buf.append("arg").append(i);

        if (null != opArgComments)
        {
          opArgComments.add("arg" + i + " Argument number " + i + " as defined by the service operation");
        }
      }
    }

    return buf.toString();
  }

  private class JavaClassWriter extends AbstractLanguageWriter implements ClassWriter, InterfaceWriter, MethodWriter
  {
    private final Writer file;

    /**
     * Constructor.
     *
     * @param folder The folder to create the file in.
     * @param className The class name.
     * @throws IOException If any problems creating the file.
     */
    public JavaClassWriter(File folder, String className) throws IOException
    {
      file = StubUtils.createLowLevelWriter(folder, className, JAVA_FILE_EXT);
    }

    /**
     * Constructor.
     *
     * @param destinationFolderName Folder to create the file in.
     * @param className The file name.
     * @throws IOException If any problems creating the file.
     */
    public JavaClassWriter(String destinationFolderName, String className) throws IOException
    {
      file = StubUtils.createLowLevelWriter(destinationFolderName, className, JAVA_FILE_EXT);
    }

    @Override
    public void addPackageStatement(String packageName) throws IOException
    {
      addPackageStatement(packageName, getConfig().getBasePackage());
    }

    @Override
    public void addStatement(String string) throws IOException
    {
      file.append(addFileStatement(0, string, false));
    }

    @Override
    public void addClassCloseStatement() throws IOException
    {
      file.append(addFileStatement(0, "}", false));
    }

    @Override
    public void addClassOpenStatement(String className, boolean finalClass, boolean abstractClass, String extendsClass, String implementsInterface, String comment) throws IOException
    {
      addMultilineComment(0, true, comment, false);

      file.append("public ");
      if (finalClass)
      {
        file.append("final ");
      }
      if (abstractClass)
      {
        file.append("abstract ");
      }
      file.append("class " + className);
      if (null != extendsClass)
      {
        file.append(" extends " + extendsClass);
      }
      if (null != implementsInterface)
      {
        file.append(" implements " + implementsInterface);
      }
      file.append(getLineSeparator());
      file.append(addFileStatement(0, "{", false));
    }

    @Override
    public void addClassVariable(boolean isStatic, boolean isFinal, String scope, String type, boolean isActual, boolean isObject, boolean isArray, String variableName, List<String> initialValue, String comment) throws IOException
    {
      StringBuilder iniVal = new StringBuilder();

      for (int i = 0; i < initialValue.size(); i++)
      {
        if (0 < i)
        {
          iniVal.append(", ");
        }

        iniVal.append(createLocalType(initialValue.get(i)));
      }

      String val;

      if (isArray)
      {
        val = iniVal.toString();
      }
      else
      {
        val = "(" + iniVal.toString() + ")";
      }

      addClassVariable(isStatic, isFinal, scope, type, isActual, isObject, isArray, variableName, val, comment);
    }

    @Override
    public void addClassVariable(boolean isStatic, boolean isFinal, String scope, String type, boolean isActual, boolean isArray, String variableName, String initialValue, String comment) throws IOException
    {
      addClassVariable(isStatic, isFinal, scope, type, isActual, false, isArray, variableName, initialValue, comment);
    }

    @Override
    public void addClassVariable(boolean isStatic, boolean isFinal, String scope, String type, boolean isActual, boolean isObject, boolean isArray, String variableName, String initialValue, String comment) throws IOException
    {
      addMultilineComment(1, false, comment, false);

      StringBuilder buf = new StringBuilder(scope);
      buf.append(" ");
      if (isStatic)
      {
        buf.append("static ");
      }
      if (isFinal)
      {
        buf.append("final ");
      }
      String ltype = createLocalType(type);
      buf.append(ltype);
      if (isArray)
      {
        buf.append("[]");
      }
      buf.append(" ");
      buf.append(variableName);

      if (null != initialValue)
      {
        if (isArray)
        {
          buf.append(" = {").append(initialValue).append("}");
        }
        else if (isNativeType(type))
        {
          NativeTypeDetails dets = getNativeType(type);
          if (dets.isObject())
          {
            buf.append(" = new ").append(ltype).append(initialValue);
          }
          else
          {
            buf.append(" = ").append(initialValue);
          }
        }
        else
        {
          buf.append(" = new ").append(ltype).append(initialValue);
        }
      }

      file.append(addFileStatement(1, buf.toString(), true));
    }

    @Override
    public void addStaticConstructor(String returnType, String methodName, String args, String constructorCall) throws IOException
    {
    }

    @Override
    public void addConstructorDefault(String className) throws IOException
    {
      addMultilineComment(1, false, "Default constructor for " + className, false);

      addConstructor(StdStrings.PUBLIC, className, "", "", null).addMethodCloseStatement();
    }

    @Override
    public MethodWriter addConstructor(String scope, String className, String args, String superArgs, String throwsSpec) throws IOException
    {
      return addConstructor(scope, className, args, superArgs, throwsSpec, null, null, null);
    }

    @Override
    public MethodWriter addConstructor(String scope, String className, String args, String superArgs, String throwsSpec, String comment, List<String> argsComments, String throwsComment) throws IOException
    {
      addMultilineComment(1, false, normaliseComments(comment, null, argsComments, Arrays.asList(throwsComment)), false);

      StringBuilder buf = new StringBuilder(scope + " " + className + "(" + args + ")");

      if (null != throwsSpec)
      {
        buf.append(" throws ");
        buf.append(throwsSpec);
      }

      file.append(addFileStatement(1, buf.toString(), false));
      file.append(addFileStatement(1, "{", false));
      if ((null != superArgs) && (0 < superArgs.length()))
      {
        file.append(addFileStatement(2, "super(" + superArgs + ")", true));
      }

      return this;
    }

    @Override
    public MethodWriter addMethodOpenStatement(boolean isConst, boolean isStatic, String scope, boolean isReturnConst, boolean isReturnActual, String rtype, String methodName, String args, String throwsSpec) throws IOException
    {
      return addMethodOpenStatement(isConst, isStatic, scope, isReturnConst, isReturnActual, rtype, methodName, args, throwsSpec, null, null, null, null);
    }

    @Override
    public MethodWriter addMethodOpenStatement(boolean isConst, boolean isStatic, String scope, boolean isReturnConst, boolean isReturnActual, String rtype, String methodName, String args, String throwsSpec, String comment, String returnComment, List<String> argsComments, List<String> throwsComment) throws IOException
    {
      return addMethodOpenStatement(false, isConst, isStatic, scope, isReturnConst, isReturnActual, rtype, methodName, args, throwsSpec, comment, returnComment, argsComments, throwsComment);
    }

    @Override
    public MethodWriter addMethodOpenStatement(boolean isVirtual, boolean isConst, boolean isStatic, String scope, boolean isReturnConst, boolean isReturnActual, String rtype, String methodName, String args, String throwsSpec) throws IOException
    {
      return addMethodOpenStatement(isVirtual, isConst, isStatic, scope, isReturnConst, isReturnActual, rtype, methodName, args, throwsSpec, null, null, null, null);
    }

    @Override
    public MethodWriter addMethodOpenStatement(boolean isVirtual, boolean isConst, boolean isStatic, String scope, boolean isReturnConst, boolean isReturnActual, String rtype, String methodName, String args, String throwsSpec, String comment, String returnComment, List<String> argsComments, List<String> throwsComment) throws IOException
    {
      return addMethodOpenStatement(false, isVirtual, isConst, isStatic, scope, isReturnConst, isReturnActual, rtype, methodName, args, throwsSpec, comment, returnComment, argsComments, throwsComment);
    }

    @Override
    public MethodWriter addMethodOpenStatement(boolean isFinal, boolean isVirtual, boolean isConst, boolean isStatic, String scope, boolean isReturnConst, boolean isReturnActual, String rtype, String methodName, String args, String throwsSpec, String comment, String returnComment, List<String> argsComments, List<String> throwsComment) throws IOException
    {
      addMultilineComment(1, false, normaliseComments(comment, returnComment, argsComments, throwsComment), false);

      String nStatic = "";

      if (isStatic)
      {
        nStatic = "static ";
      }

      String nFinal = "";

      if (isFinal)
      {
        nFinal = "final ";
      }

      rtype = createLocalType(rtype);
      args = processArgTypes(args);

      StringBuilder buf = new StringBuilder(scope + " " + nStatic + nFinal + rtype + " " + methodName + "(" + args + ")");

      if (null != throwsSpec)
      {
        buf.append(" throws ");
        buf.append(throwsSpec);
      }

      file.append(addFileStatement(1, buf.toString(), false));
      file.append(addFileStatement(1, "{", false));

      return this;
    }

    @Override
    public void addPackageStatement(String packageName, String prefix) throws IOException
    {
      file.append(addFileStatement(0, "package " + prefix + packageName, true));
    }

    @Override
    public void flush() throws IOException
    {
      file.flush();
    }

    @Override
    public void addInterfaceCloseStatement() throws IOException
    {
      file.append(addFileStatement(0, "}", false));
    }

    @Override
    public void addInterfaceMethodDeclaration(String scope, String rtype, String methodName, String args, String throwsSpec, String comment, String returnComment, List<String> argsComments, List<String> throwsComment) throws IOException
    {
      rtype = createLocalType(rtype);
      args = processArgTypes(args);

      addMultilineComment(1, false, normaliseComments(comment, returnComment, argsComments, throwsComment), false);

      StringBuilder buf = new StringBuilder(rtype + " " + methodName + "(" + args + ")");

      if (null != throwsSpec)
      {
        buf.append(" throws ");
        buf.append(throwsSpec);
      }

      file.append(addFileStatement(1, buf.toString(), true));
    }

    @Override
    public void addInterfaceOpenStatement(String interfaceName, String extendsInterface, String comment) throws IOException
    {
      addMultilineComment(0, true, comment, false);

      file.append("public interface ");
      file.append(interfaceName);
      if (null != extendsInterface)
      {
        file.append(" extends " + extendsInterface);
      }
      file.append(getLineSeparator());
      file.append(addFileStatement(0, "{", false));
    }

    @Override
    public void addArrayMethodStatement(String arrayVariable, String indexVariable, String arrayMaxSize) throws IOException
    {
      addMethodStatement("return " + arrayVariable + "[" + indexVariable + "]", true);
    }

    @Override
    public void addSuperMethodStatement(String method, String args) throws IOException
    {
      addMethodStatement("super." + method + "(" + args + ")", true);
    }

    @Override
    public void addMethodStatement(String statement) throws IOException
    {
      addMethodStatement(statement, true);
    }

    @Override
    public void addMethodWithDependencyStatement(String statement, String dependency, boolean addSemi) throws IOException
    {
      addMethodStatement(statement, addSemi);
    }

    @Override
    public void addMethodStatement(String statement, boolean addSemi) throws IOException
    {
      if (0 < statement.trim().length())
      {
        file.append(addFileStatement(2, statement, addSemi));
      }
    }

    @Override
    public void addMethodCloseStatement() throws IOException
    {
      file.append(addFileStatement(1, "}", false));
      file.append(getLineSeparator());
    }

    @Override
    public void addMultilineComment(int tabCount, boolean preBlankLine, List<String> comments, boolean postBlankLine) throws IOException
    {
      if (0 < comments.size())
      {
        if (preBlankLine)
        {
          file.append(getLineSeparator());
        }

        file.append(addFileStatement(tabCount, "/**", false));
        for (String comment : comments)
        {
          file.append(addFileStatement(tabCount, " * " + comment, false));
        }
        file.append(addFileStatement(tabCount, " */", false));

        if (postBlankLine)
        {
          file.append(getLineSeparator());
        }
      }
    }

    private List<String> normaliseComments(String comment, String returnComment, List<String> argsComments, List<String> throwsComment)
    {
      List<String> rv = new LinkedList<String>();

      normaliseComment(rv, comment);
      normaliseComments(rv, StubUtils.conditionalAdd("@param ", argsComments));
      normaliseComment(rv, StubUtils.conditionalAdd("@return ", returnComment));
      normaliseComments(rv, StubUtils.conditionalAdd("@throws ", throwsComment));

      return rv;
    }

    private String processArgTypes(String argStr)
    {
      StringBuilder buf = new StringBuilder();
      if (0 < argStr.length())
      {
        String[] args = argStr.split(",");

        for (int i = 0; i < args.length; i++)
        {
          String string = args[i].trim();
          int spaceIndex = string.lastIndexOf(' ');
          String type = string.substring(0, spaceIndex);
          String name = checkForReservedWords(string.substring(spaceIndex + 1));

          if (i > 0)
          {
            buf.append(", ");
          }

          buf.append(createLocalType(type));
          buf.append(" ");
          buf.append(name);
        }
      }

      return buf.toString();
    }

    private String createLocalType(String type)
    {
      if (null != type)
      {
        String head = "";
        String tail = "";

        if (type.startsWith("new "))
        {
          head = "new ";
          int tindex = type.indexOf('(');

          if (tindex == -1)
          {
            tindex = type.indexOf('[');
          }

          tail = type.substring(tindex);
          type = type.substring(4, type.length() - tail.length());
        }

        if (isNativeType(type))
        {
          NativeTypeDetails dets = getNativeType(type);

          type = dets.getLanguageTypeName();
        }
        else
        {
          AttributeTypeDetails dets = getAttributeDetails(type);
          if (null != dets)
          {
            type = dets.getTargetType();
          }
        }

        return head + type + tail;
      }

      return type;
    }
  }
}