/*
Copyright (c) 2012, Adam Retter
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of Adam Retter Consulting nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL Adam Retter BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.exquery.restxq.impl.annotation;

import java.io.InputStream;
import org.exquery.http.HttpRequest;
import org.exquery.restxq.RESTXQErrorCodes;
import org.exquery.restxq.RESTXQErrorCodes.RESTXQErrorCode;
import org.exquery.xquery.Literal;
import org.exquery.xquery.Type;
import org.exquery.xquery.TypedArgumentValue;
import org.exquery.xquery.TypedValue;

/**
 * Implementation of RESTXQ Form Parameter Annotation
 * i.e. %rest:form-param
 *
 * @author Adam Retter <adam.retter@googlemail.com>
 */
public class FormParameterAnnotation extends AbstractParameterAnnotation {
    
    /**
     * @see AbstractParameterAnnotation#canProvideDefaultValue()
     * 
     * @return Always returns true
     */
    @Override
    protected boolean canProvideDefaultValue() {
        return true;
    }
    
    /**
     * @see AbstractParameterAnnotation#extractParameter(org.exquery.http.HttpRequest)
     */
    @Override
    public TypedArgumentValue extractParameter(final HttpRequest request) {
        
        
        /* Taken from section 6.1, page 31 of the JAX-RS JSR-311 specification:
         * 
         * Servlet ﬁlters may trigger consumption of a request body by accessing request parameters. In a servlet
         * container the @FormParam annotation and the standard entity provider for application/x-www-form--
         * urlencoded MUST obtain their values from the servlet request parameters if the request body has already
         * been consumed. Servlet APIs do not differentiate between parameters in the URI and body of a request so
         * URI-based query parameters may be included in the entity parameter
         */
        
        return new TypedArgumentValue() {

            @Override
            public String getArgumentName() {
                return getParameterAnnotationMapping().getFunctionArgumentName();
            }

            @Override
            public TypedValue getTypedValue() {
                final Object formParam = request.getFormParam(getParameterAnnotationMapping().getParameterName());
                if(formParam == null) {
                    final Literal defaultLiteral = getParameterAnnotationMapping().getDefaultValue();
                    return new TypedValue<String>() {

                        @Override
                        public Type getType() {
                            return defaultLiteral.getType();
                        }

                        @Override
                        public String getValue() {
                            return defaultLiteral.getValue();
                        }
                    };
                }
                
                if(formParam instanceof String) {
                    return new TypedValue<String>() {

                        @Override
                        public Type getType() {
                            return Type.STRING;
                        }

                        @Override
                        public String getValue() {
                            return (String)formParam;
                        }
                    };
                }
                
                //TODO cope with the situation whereby there may be more than a single value
                /*
                if(formField instanceof List) {
                    final List<String> fieldValues = (List<String>)formField;
                    final ValueSequence vals = new ValueSequence();
                    for(String fieldValue : fieldValues) {
                        vals.add(new StringValue(fieldValue));
                    }
                    
                    return vals;
                }*/
                
                if(formParam instanceof InputStream) {
                    /*try {
                        return BinaryValueFromInputStream.getInstance(context, new Base64BinaryValueType(), (InputStream)formParam);
                    } catch(XPathException xpe) {
                        //TODO log
                        return null;
                    }*/
                    return new TypedValue<InputStream>() {

                        @Override
                        public Type getType() {
                            return Type.BASE64_BINARY;
                        }

                        @Override
                        public InputStream getValue() {
                            return (InputStream)formParam;
                        }
                    };
                }
                
                return null;
            }
        };
    }

    //<editor-fold desc="Error Codes">
    
    /**
     * @see AbstractParameterAnnotation#getInvalidAnnotationParamsErr()
     */
    @Override
    final protected RESTXQErrorCode getInvalidAnnotationParamsErr() {
        return RESTXQErrorCodes.RQST0014;
    }

    /**
     * @see AbstractParameterAnnotation#getInvalidKeyErr()
     */
    @Override
    final protected RESTXQErrorCode getInvalidParameterNameErr() {
        return RESTXQErrorCodes.RQST0015;
    }

    /**
     * @see AbstractParameterAnnotation#getInvalidValueErr()
     */
    @Override
    final protected RESTXQErrorCode getInvalidFunctionArgumentNameErr() {
        return RESTXQErrorCodes.RQST0016;
    }

    /**
     * @see AbstractParameterAnnotation#getInvalidDefaultValueErr()
     */
    @Override
    final protected RESTXQErrorCode getInvalidDefaultValueErr() {
        return RESTXQErrorCodes.RQST0017;
    }

    /**
     * @see AbstractParameterAnnotation#getInvalidDefaultValueTypeErr()
     */
    @Override
    protected RESTXQErrorCode getInvalidDefaultValueTypeErr() {
        return RESTXQErrorCodes.RQST0018;
    }
    
    /**
     * @see AbstractParameterAnnotation#getInvalidAnnotationParamSyntaxErr()
     */
    @Override
    protected RESTXQErrorCode getInvalidAnnotationParametersSyntaxErr() {
        return RESTXQErrorCodes.RQST0019;
    }
    
    //<editor-fold>
}