/*
 * This file is part of ionChannel.
 *
 * ionChannel is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, version 3.
 *
 * ionChannel is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with ionChannel.  If not, see <https://www.gnu.org/licenses/>.
 */

package social.ionch.api;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigInteger;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonArray;
import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonGrammar;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.JsonPrimitive;
import blue.endless.jankson.api.DeserializationException;
import blue.endless.jankson.api.DeserializerFunction;
import blue.endless.jankson.api.Marshaller;
import blue.endless.jankson.api.SyntaxError;

/**
 * A Jankson-like API to the nanojson strict JSON parser, modified to return Jankson objects.
 * Output from this modified nanojson is then passed to Jankson for POJO deserialization.
 * @see JsonGrammar#STRICT
 */
public class Nojankson {
	private final Jankson jank;
	
	private Nojankson(Jankson jank) {
		this.jank = jank;
	}
	
	/**
	 * @see Jankson#load(String)
	 */
	@Nonnull
	public JsonObject load(String s) throws SyntaxError {
		return JsonParser.object().from(s);
	}
	
	/**
	 * @see Jankson#load(File)
	 */
	@Nonnull
	public JsonObject load(File f) throws IOException, SyntaxError {
		try(InputStream in = new FileInputStream(f)) {
			return load(in);
		}
	}
	
	/**
	 * @see Jankson#load(InputStream)
	 */
	@Nonnull
	public JsonObject load(InputStream in) throws IOException, SyntaxError {
		return JsonParser.object().from(in);
	}
	
	/**
	 * @see Jankson#loadElement(String)
	 */
	@Nonnull
	public JsonElement loadElement(String s) throws SyntaxError {
		return JsonParser.any().from(s);
	}
	
	/**
	 * @see Jankson#loadElement(File)
	 */
	@Nonnull
	public JsonElement loadElement(File f) throws IOException, SyntaxError {
		try(InputStream in = new FileInputStream(f)) {
			return loadElement(in);
		}
	}
	
	/**
	 * @see Jankson#loadElement(InputStream)
	 */
	@Nonnull
	public JsonElement loadElement(InputStream in) throws IOException, SyntaxError {
		return JsonParser.any().from(in);
	}
	
	/**
	 * @see Jankson#fromJson(JsonObject, Class)
	 */
	public <T> T fromJson(JsonObject obj, Class<T> clazz) {
		return jank.fromJson(obj, clazz);
	}
	
	/**
	 * @see Jankson#fromJson(String, Class)
	 */
	public <T> T fromJson(String json, Class<T> clazz) throws SyntaxError {
		JsonObject obj = load(json);
		return fromJson(obj, clazz);
	}
	
	/**
	 * @see Jankson#fromJsonCarefully(String, Class)
	 */
	public <T> T fromJsonCarefully(String json, Class<T> clazz) throws SyntaxError, DeserializationException {
		JsonObject obj = load(json);
		return fromJsonCarefully(obj, clazz);
	}
	
	/**
	 * @see Jankson#fromJsonCarefully(JsonObject, Class)
	 */
	public <T> T fromJsonCarefully(JsonObject obj, Class<T> clazz) throws DeserializationException {
		return jank.fromJsonCarefully(obj, clazz);
	}
	
	/**
	 * @see Jankson#toJson(Object)
	 */
	public <T> JsonElement toJson(T t) {
		return jank.toJson(t);
	}
	
	/**
	 * @see Jankson#toJson(Object, Marshaller)
	 */
	public <T> JsonElement toJson(T t, Marshaller alternateMarshaller) {
		return jank.toJson(t, alternateMarshaller);
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder {
		private final Jankson.Builder jank = Jankson.builder();
		
		/**
		 * @see Jankson.Builder#registerSerializer
		 */
		public <T> Builder registerSerializer(Class<T> clazz, BiFunction<T, blue.endless.jankson.api.Marshaller, JsonElement> serializer) {
			jank.registerSerializer(clazz, serializer);
			return this;
		}
		
		/**
		 * @see Jankson.Builder#registerDeserializer
		 */
		public <A,B> Builder registerDeserializer(Class<A> sourceClass, Class<B> targetClass, DeserializerFunction<A,B> function) {
			jank.registerDeserializer(sourceClass, targetClass, function);
			return this;
		}
		
		/**
		 * @see Jankson.Builder#registerTypeFactory
		 */
		public <T> Builder registerTypeFactory(Class<T> clazz, Supplier<T> factory) {
			jank.registerTypeFactory(clazz, factory);
			return this;
		}
		
		public Nojankson build() {
			return new Nojankson(jank.build());
		}
	}
	
	/**
	 * Simple strict JSON parser based on <a href="https://github.com/mmastrac/nanojson">nanojson</a>,
	 * adapted to use Jankson's objects.
	 * <pre>
	 * Copyright 2011 The nanojson Authors
	 *
	 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
	 * use this file except in compliance with the License. You may obtain a copy of
	 * the License at
	 *
	 * http://www.apache.org/licenses/LICENSE-2.0
	 *
	 * Unless required by applicable law or agreed to in writing, software
	 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
	 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
	 * License for the specific language governing permissions and limitations under
	 * the License.
	 * </pre>
	 */
	private static final class JsonParser {
		private JsonElement value;
		private int token;

		private JsonTokener tokener;

		public static final class JsonParserContext<T> {
			private final Class<T> clazz;

			JsonParserContext(Class<T> clazz) {
				this.clazz = clazz;
			}

			public T from(String s) throws SyntaxError {
				try {
					return new JsonParser(new JsonTokener(new StringReader(s))).parse(clazz);
				} catch (IOException e) {
					throw new AssertionError(e);
				}
			}

			public T from(InputStream stm) throws SyntaxError, IOException {
				return new JsonParser(new JsonTokener(stm)).parse(clazz);
			}
		}

		JsonParser(JsonTokener tokener) throws SyntaxError {
			this.tokener = tokener;
		}

		public static JsonParserContext<JsonObject> object() {
			return new JsonParserContext<>(JsonObject.class);
		}

		public static JsonParserContext<JsonElement> any() {
			return new JsonParserContext<>(JsonElement.class);
		}

		<T> T parse(Class<T> clazz) throws SyntaxError {
			advanceToken();
			Object parsed = currentValue();
			if (advanceToken() != JsonTokener.TOKEN_EOF)
				throw tokener.createParseException(null, "Expected end of input, got " + token, true);
			if (clazz != Object.class && (parsed == null || !clazz.isAssignableFrom(parsed.getClass())))
				throw tokener.createParseException(null,
						"JSON did not contain the correct type, expected " + clazz.getSimpleName() + ".",
						true);
			return clazz.cast(parsed);
		}

		private JsonElement currentValue() throws SyntaxError {
			// Only a value start token should appear when we're in the context of parsing a JSON value
			if (token >= JsonTokener.TOKEN_VALUE_MIN && value instanceof JsonElement)
				return value;
			throw tokener.createParseException(null, "Expected JSON value, got " + token, true);
		}

		private int advanceToken() throws SyntaxError {
			token = tokener.advanceToToken();
			switch (token) {
			case JsonTokener.TOKEN_ARRAY_START: // Inlined function to avoid additional stack
				JsonArray list = new JsonArray();
				if (advanceToken() != JsonTokener.TOKEN_ARRAY_END)
					while (true) {
						list.add(currentValue());
						if (advanceToken() == JsonTokener.TOKEN_ARRAY_END)
							break;
						if (token != JsonTokener.TOKEN_COMMA)
							throw tokener.createParseException(null,
									"Expected a comma or end of the array instead of " + token, true);
						if (advanceToken() == JsonTokener.TOKEN_ARRAY_END)
							throw tokener.createParseException(null, "Trailing comma found in array", true);
					}
				value = list;
				return token = JsonTokener.TOKEN_ARRAY_START;
			case JsonTokener.TOKEN_OBJECT_START: // Inlined function to avoid additional stack
				JsonObject map = new JsonObject();
				if (advanceToken() != JsonTokener.TOKEN_OBJECT_END)
					while (true) {
						if (token != JsonTokener.TOKEN_STRING)
							throw tokener.createParseException(null, "Expected STRING, got " + token, true);
						String key = ((JsonPrimitive)value).asString();
						if (advanceToken() != JsonTokener.TOKEN_COLON)
							throw tokener.createParseException(null, "Expected COLON, got " + token, true);
						advanceToken();
						map.put(key, currentValue());
						if (advanceToken() == JsonTokener.TOKEN_OBJECT_END)
							break;
						if (token != JsonTokener.TOKEN_COMMA)
							throw tokener.createParseException(null,
									"Expected a comma or end of the object instead of " + token, true);
						if (advanceToken() == JsonTokener.TOKEN_OBJECT_END)
							throw tokener.createParseException(null, "Trailing object found in array", true);
					}
				value = map;
				return token = JsonTokener.TOKEN_OBJECT_START;
			case JsonTokener.TOKEN_TRUE:
				value = JsonPrimitive.TRUE;
				break;
			case JsonTokener.TOKEN_FALSE:
				value = JsonPrimitive.FALSE;
				break;
			case JsonTokener.TOKEN_NULL:
				value = null;
				break;
			case JsonTokener.TOKEN_STRING:
				value = new JsonPrimitive(tokener.reusableBuffer.toString());
				break;
			case JsonTokener.TOKEN_NUMBER:
				value = new JsonPrimitive(parseNumber());
				break;
			default:
			}

			return token;
		}

		private Number parseNumber() throws SyntaxError {
			String number = tokener.reusableBuffer.toString();

			try {
				if (tokener.isDouble)
					return Double.parseDouble(number);

				// Quick parse for single-digits
				if (number.length() == 1) {
					return number.charAt(0) - '0';
				} else if (number.length() == 2 && number.charAt(0) == '-') {
					return '0' - number.charAt(1);
				}

				// HACK: Attempt to parse using the approximate best type for this
				boolean firstMinus = number.charAt(0) == '-';
				int length = firstMinus ? number.length() - 1 : number.length();
				// CHECKSTYLE_OFF: MagicNumber
				if (length < 10 || (length == 10 && number.charAt(firstMinus ? 1 : 0) < '2')) // 2 147 483 647
					return Integer.parseInt(number);
				if (length < 19 || (length == 19 && number.charAt(firstMinus ? 1 : 0) < '9')) // 9 223 372 036 854 775 807
					return Long.parseLong(number);
				// CHECKSTYLE_ON: MagicNumber
				return new BigInteger(number);
			} catch (NumberFormatException e) {
				throw tokener.createParseException(e, "Malformed number: " + number, true);
			}
		}
		
		private static final class JsonTokener {
			// Used by tests
			static final int BUFFER_SIZE = 32 * 1024;

			static final int BUFFER_ROOM = 256;

			private int linePos = 1, rowPos, charOffset, utf8adjust;
			private int tokenCharPos, tokenCharOffset;

			private boolean eof;
			private int index;
			private final Reader reader;
			private final char[] buffer = new char[BUFFER_SIZE];
			private int bufferLength;

			private final boolean utf8;

			protected StringBuilder reusableBuffer = new StringBuilder();
			protected boolean isDouble;

			static final char[] TRUE = { 'r', 'u', 'e' };
			static final char[] FALSE = { 'a', 'l', 's', 'e' };
			static final char[] NULL = { 'u', 'l', 'l' };

			static final int TOKEN_EOF = 0;
			static final int TOKEN_COMMA = 1;
			static final int TOKEN_COLON = 2;
			static final int TOKEN_OBJECT_END = 3;
			static final int TOKEN_ARRAY_END = 4;
			static final int TOKEN_NULL = 5;
			static final int TOKEN_TRUE = 6;
			static final int TOKEN_FALSE = 7;
			static final int TOKEN_STRING = 8;
			static final int TOKEN_NUMBER = 9;
			static final int TOKEN_OBJECT_START = 10;
			static final int TOKEN_ARRAY_START = 11;
			static final int TOKEN_VALUE_MIN = TOKEN_NULL;

			private static final class PseudoUtf8Reader extends Reader {
				private final InputStream buffered;
				private byte[] buf = new byte[BUFFER_SIZE];

				PseudoUtf8Reader(InputStream buffered) {
					this.buffered = buffered;
				}

				@Override
				public int read(char[] cbuf, int off, int len) throws IOException {
					int r = buffered.read(buf, off, len);
					for (int i = off; i < off + r; i++)
						cbuf[i] = (char)buf[i];
					return r;
				}

				@Override
				public void close() throws IOException {
				}
			}
			
			JsonTokener(Reader reader) throws SyntaxError, IOException {
				this.reader = reader;
				this.utf8 = false;
				init();
			}
			
			JsonTokener(InputStream stm) throws SyntaxError, IOException {
				final InputStream buffered = (stm instanceof BufferedInputStream || stm instanceof ByteArrayInputStream)
						? stm
						: new BufferedInputStream(stm);
				this.reader = new PseudoUtf8Reader(buffered);
				this.utf8 = true;
				init();
			}

			private void init() throws SyntaxError {
				eof = refillBuffer();
				consumeWhitespace();
			}

			void consumeKeyword(char first, char[] expected) throws SyntaxError {
				if (ensureBuffer(expected.length) < expected.length) {
					throw createHelpfulException(first, expected, 0);
				}

				for (int i = 0; i < expected.length; i++)
					if (buffer[index++] != expected[i])
						throw createHelpfulException(first, expected, i);

				fixupAfterRawBufferRead();

				// The token should end with something other than an ASCII letter
				if (isAsciiLetter(peekChar()))
					throw createHelpfulException(first, expected, expected.length);
			}

			void consumeTokenNumber(char savedChar) throws SyntaxError {
				reusableBuffer.setLength(0);
				reusableBuffer.append(savedChar);
				isDouble = false;

				// The JSON spec is way stricter about number formats than
				// Double.parseDouble(). This is a hand-rolled pseudo-parser that
				// verifies numbers we read.
				int state;
				if (savedChar == '-') {
					state = 1;
				} else if (savedChar == '0') {
					state = 3;
				} else {
					state = 2;
				}
				
				outer: while (true) {
					int n = ensureBuffer(BUFFER_ROOM);
					if (n == 0)
						break outer;

					for (int i = 0; i < n; i++) {
						char nc = buffer[index];
						if (!isDigitCharacter(nc))
							break outer;

						int ns = -1;
						sw:
						switch (state) {
						case 1: // start leading negative
							if (nc == '-' && state == 0) {
								ns = 1; break sw;
							}
							if (nc == '0') {
								ns = 3; break sw;
							}
							if (nc >= '0' && nc <= '9') {
								ns = 2; break sw;
							}
							break;
						case 2: // no leading zero
						case 3: // leading zero
							if ((nc >= '0' && nc <= '9') && state == 2) {
								ns = 2; break sw;
							}
							if (nc == '.') {
								isDouble = true;
								ns = 4; break sw;
							}
							if (nc == 'e' || nc == 'E') {
								isDouble = true;
								ns = 6; break sw;
							}
							break;
						case 4: // after period
						case 5: // after period, one digit read
							if (nc >= '0' && nc <= '9') {
								ns = 5; break sw;
							}
							if ((nc == 'e' || nc == 'E') && state == 5) {
								isDouble = true;
								ns = 6; break sw;
							}
							break;
						case 6: // after exponent
						case 7: // after exponent and sign
							if (nc == '+' || nc == '-' && state == 6) {
								ns = 7; break sw;
							}
							if (nc >= '0' && nc <= '9') {
								ns = 8; break sw;
							}
							break;
						case 8: // after digits
							if (nc >= '0' && nc <= '9') {
								ns = 8; break sw;
							}
							break;
						default:
							assert false : "Impossible"; // will throw malformed number
						}
						reusableBuffer.append(nc);
						index++;
						if (ns == -1)
							throw createParseException(null, "Malformed number: " + reusableBuffer, true);
						state = ns;
					}
				}
				
				if (state != 2 && state != 3 && state != 5 && state != 8)
					throw createParseException(null, "Malformed number: " + reusableBuffer, true);
				
				// Special case for -0
				if (state == 3 && savedChar == '-')
					isDouble = true;
				
				fixupAfterRawBufferRead();
			}

			void consumeTokenString() throws SyntaxError {
				reusableBuffer.setLength(0);
				
				start:
				while (true) {
					int n = ensureBuffer(BUFFER_ROOM);
					if (n == 0)
						throw createParseException(null, "String was not terminated before end of input", true);
					
					for (int i = 0; i < n; i++) {
						char c = stringChar();
						if (c == '"') {
							fixupAfterRawBufferRead();
							reusableBuffer.append(buffer, index - i - 1, i);
							return;
						}
						if (c == '\\' || (utf8 && (c & 0x80) != 0)) {
							reusableBuffer.append(buffer, index - i - 1, i);
							index--;
							break start;
						}
					}
					
					reusableBuffer.append(buffer, index - n, n);
				}
				
				outer: while (true) {
					int n = ensureBuffer(BUFFER_ROOM);
					if (n == 0)
						throw createParseException(null, "String was not terminated before end of input", true);
			
					int end = index + n;
					while (index < end) {
						char c = stringChar();
						
						if (utf8 && (c & 0x80) != 0) {
							// If it's a UTF-8 codepoint, we know it won't have special meaning
							consumeTokenStringUtf8Char(c);
							continue outer;
						}
			
						switch (c) {
						case '\"':
							fixupAfterRawBufferRead();
							return;
						case '\\':
							char escape = buffer[index++];
							switch (escape) {
							case 'b':
								reusableBuffer.append('\b');
								break;
							case 'f':
								reusableBuffer.append('\f');
								break;
							case 'n':
								reusableBuffer.append('\n');
								break;
							case 'r':
								reusableBuffer.append('\r');
								break;
							case 't':
								reusableBuffer.append('\t');
								break;
							case '"':
							case '/':
							case '\\':
								reusableBuffer.append(escape);
								break;
							case 'u':
								int escaped = 0;
			
								for (int j = 0; j < 4; j++) {
									escaped <<= 4;
									int digit = buffer[index++];
									if (digit >= '0' && digit <= '9') {
										escaped |= (digit - '0');
									} else if (digit >= 'A' && digit <= 'F') {
										escaped |= (digit - 'A') + 10;
									} else if (digit >= 'a' && digit <= 'f') {
										escaped |= (digit - 'a') + 10;
									} else {
										throw createParseException(null, "Expected unicode hex escape character: " + (char)digit
												+ " (" + digit + ")", false);
									}
								}
			
								reusableBuffer.append((char)escaped);
								break;
							default:
								throw createParseException(null, "Invalid escape: \\" + escape, false);
							}
							break;
						default:
							reusableBuffer.append(c);
						}
					}
					
					if (index > bufferLength) {
						index = bufferLength; // Reset index to last valid location
						throw createParseException(null,
								"EOF encountered in the middle of a string escape",
								false);
					}
				}
			}

			@SuppressWarnings("fallthrough")
			private void consumeTokenStringUtf8Char(char c) throws SyntaxError {
				ensureBuffer(5);

				// Hand-UTF8-decoding
				switch (c & 0xf0) {
				case 0x80:
				case 0x90:
				case 0xa0:
				case 0xb0:
					throw createParseException(null,
							"Illegal UTF-8 continuation byte: 0x" + Integer.toHexString(c & 0xff), false);
				case 0xc0:
					// Check for illegal C0 and C1 bytes
					if ((c & 0xe) == 0)
						throw createParseException(null, "Illegal UTF-8 byte: 0x" + Integer.toHexString(c & 0xff),
								false);
					// fall-through
				case 0xd0:
					c = (char)((c & 0x1f) << 6 | (buffer[index++] & 0x3f));
					reusableBuffer.append(c);
					utf8adjust++;
					break;
				case 0xe0:
					c = (char)((c & 0x0f) << 12 | (buffer[index++] & 0x3f) << 6 | (buffer[index++] & 0x3f));
					utf8adjust += 2;
					// Check for illegally-encoded surrogate - http://unicode.org/faq/utf_bom.html#utf8-4
					if ((c >= '\ud800' && c <= '\udbff') || (c >= '\udc00' && c <= '\udfff'))
						throw createParseException(null, "Illegal UTF-8 codepoint: 0x" + Integer.toHexString(c),
								false);
					reusableBuffer.append(c);
					break;
				case 0xf0:
					if ((c & 0xf) >= 5)
						throw createParseException(null, "Illegal UTF-8 byte: 0x" + Integer.toHexString(c & 0xff),
								false);

					// Extended char
					switch ((c & 0xc) >> 2) {
					case 0:
					case 1:
						reusableBuffer.appendCodePoint((c & 7) << 18 | (buffer[index++] & 0x3f) << 12
								| (buffer[index++] & 0x3f) << 6 | (buffer[index++] & 0x3f));
						utf8adjust += 3;
						break;
					case 2:
						// TODO: \uFFFD (replacement char)
						int codepoint = (c & 3) << 24 | (buffer[index++] & 0x3f) << 18 | (buffer[index++] & 0x3f) << 12
								| (buffer[index++] & 0x3f) << 6 | (buffer[index++] & 0x3f);
						throw createParseException(null,
								"Unable to represent codepoint 0x" + Integer.toHexString(codepoint)
										+ " in a Java string", false);
					case 3:
						codepoint = (c & 1) << 30 | (buffer[index++] & 0x3f) << 24 | (buffer[index++] & 0x3f) << 18
								| (buffer[index++] & 0x3f) << 12 | (buffer[index++] & 0x3f) << 6
								| (buffer[index++] & 0x3f);
						throw createParseException(null,
								"Unable to represent codepoint 0x" + Integer.toHexString(codepoint)
										+ " in a Java string", false);
					default:
						assert false : "Impossible";
					}
					break;
				default:
					// Regular old byte
					break;
				}
				if (index > bufferLength)
					throw createParseException(null, "UTF-8 codepoint was truncated", false);
			}

			private char stringChar() throws SyntaxError {
				char c = buffer[index++];
				if (c < 32)
					throwControlCharacterException(c);
				return c;
			}

			private void throwControlCharacterException(char c) throws SyntaxError {
				// Need to ensure that we position this at the correct location for the error
				if (c == '\n') {
					linePos++;
					rowPos = index + 1 + charOffset;
					utf8adjust = 0;
				}
				throw createParseException(null,
						"Strings may not contain control characters: 0x" + Integer.toString(c, 16), false);
			}

			private boolean isDigitCharacter(int c) {
				return (c >= '0' && c <= '9') || c == 'e' || c == 'E' || c == '.' || c == '+' || c == '-';
			}

			boolean isWhitespace(int c) {
				return c == ' ' || c == '\n' || c == '\r' || c == '\t';
			}

			boolean isAsciiLetter(int c) {
				return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z');
			}

			private boolean refillBuffer() throws SyntaxError {
				try {
					int r = reader.read(buffer, 0, buffer.length);
					if (r <= 0) {
						return true;
					}
					charOffset += bufferLength;
					index = 0;
					bufferLength = r;
					return false;
				} catch (IOException e) {
					throw createParseException(e, "IOException", true);
				}
			}

			private int peekChar() {
				return eof ? -1 : buffer[index];
			}

			int ensureBuffer(int n) throws SyntaxError {
				// We're good here
				if (bufferLength - n >= index) {
					return n;
				}

				// Nope, we need to read more, but we also have to retain whatever buffer we have
				if (index > 0) {
					charOffset += index;
					bufferLength = bufferLength - index;
					System.arraycopy(buffer, index, buffer, 0, bufferLength);
					index = 0;
				}
				try {
					while (buffer.length > bufferLength) {
						int r = reader.read(buffer, bufferLength, buffer.length - bufferLength);
						if (r <= 0) {
							return bufferLength - index;
						}
						bufferLength += r;
						if (bufferLength > n)
							return bufferLength - index;
					}

					// Should be impossible
					assert false : "Unexpected internal error";
					throw new IOException("Unexpected internal error");
				} catch (IOException e) {
					throw createParseException(e, "IOException", true);
				}
			}

			private int advanceChar() throws SyntaxError {
				if (eof)
					return -1;

				int c = buffer[index];
				if (c == '\n') {
					linePos++;
					rowPos = index + 1 + charOffset;
					utf8adjust = 0;
				}

				index++;

				// Prepare for next read
				if (index >= bufferLength)
					eof = refillBuffer();

				return c;
			}
			
			private void consumeWhitespace() throws SyntaxError {
				int n;
				do {
					n = ensureBuffer(BUFFER_ROOM);
					for (int i = 0; i < n; i++) {
						char c = buffer[index];
						if (!isWhitespace(c)) {
							fixupAfterRawBufferRead();
							return;
						}
						if (c == '\n') {
							linePos++;
							rowPos = index + 1 + charOffset;
							utf8adjust = 0;
						}
						index++;
					}
				} while (n > 0);
				eof = true;
			}
			
			int advanceToToken() throws SyntaxError {
				int c = advanceChar();
				while (isWhitespace(c))
					c = advanceChar();

				tokenCharPos = index + charOffset - rowPos - utf8adjust;
				tokenCharOffset = charOffset + index;
				
				int token;
				switch (c) {
				case -1:
					return TOKEN_EOF;
				case '[':
					token = TOKEN_ARRAY_START;
					break;
				case ']':
					token = TOKEN_ARRAY_END;
					break;
				case ',':
					token = TOKEN_COMMA;
					break;
				case ':':
					token = TOKEN_COLON;
					break;
				case '{':
					token = TOKEN_OBJECT_START;
					break;
				case '}':
					token = TOKEN_OBJECT_END;
					break;
				case 't':
					consumeKeyword((char)c, JsonTokener.TRUE);
					token = TOKEN_TRUE;
					break;
				case 'f':
					consumeKeyword((char)c, JsonTokener.FALSE);
					token = TOKEN_FALSE;
					break;
				case 'n':
					consumeKeyword((char)c, JsonTokener.NULL);
					token = TOKEN_NULL;
					break;
				case '\"':
					consumeTokenString();
					token = TOKEN_STRING;
					break;
				case '-':
				case '0':
				case '1':
				case '2':
				case '3':
				case '4':
				case '5':
				case '6':
				case '7':
				case '8':
				case '9':
					consumeTokenNumber((char)c);
					token = TOKEN_NUMBER;
					break;
				case '+':
				case '.':
					throw createParseException(null, "Numbers may not start with '" + (char)c + "'", true);
				default:
					if (isAsciiLetter(c))
						throw createHelpfulException((char)c, null, 0);

					throw createParseException(null, "Unexpected character: " + (char)c, true);
				}
				
//				consumeWhitespace();
				return token;
			}

			void fixupAfterRawBufferRead() throws SyntaxError {
				if (index >= bufferLength)
					eof = refillBuffer();
			}

			SyntaxError createHelpfulException(char first, char[] expected, int failurePosition)
					throws SyntaxError {
				// Build the first part of the token
				StringBuilder errorToken = new StringBuilder(first
						+ (expected == null ? "" : new String(expected, 0, failurePosition)));

				// Consume the whole pseudo-token to make a better error message
				while (isAsciiLetter(peekChar()) && errorToken.length() < 15)
					errorToken.append((char)advanceChar());

				return createParseException(null, "Unexpected token '" + errorToken + "'"
						+ (expected == null ? "" : ". Did you mean '" + first + new String(expected) + "'?"), true);
			}

			SyntaxError createParseException(Exception e, String message, boolean tokenPos) {
				if (tokenPos) {
					SyntaxError se = new SyntaxError(message);
					se.setStartParsing(linePos, tokenCharPos);
					se.setEndParsing(linePos, tokenCharPos+tokenCharOffset);
					return se;
				} else {
					int charPos = Math.max(1, index + charOffset - rowPos - utf8adjust);
					SyntaxError se = new SyntaxError(message);
					se.setEndParsing(linePos, charPos);
					return se;
				}
			}
		}
	}
	
}
