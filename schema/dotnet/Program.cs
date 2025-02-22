﻿using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using NJsonSchema;
using NJsonSchema.CodeGeneration.CSharp;
using NJsonSchema.Yaml;

namespace Robocode.TankRoyale.SchemaCodeGenerator
{
    /// <summary>
    /// CLI program for generating C# source files from the Robocode Tank Royale JSON Schema files.
    /// </summary>
    class Program
    {
        static void Main(string[] args)
        {
            // The program needs 2 arguments: the source path for the location of the JSON Schema files, and the
            // destination path for storing the generated source files.
            if (args.Length < 2)
            {
                string appName = System.Reflection.Assembly.GetExecutingAssembly().GetName().Name;

                Console.WriteLine("Generates C# source files from the Robocode Tank Royale JSON schema files.");
                Console.WriteLine();
                Console.WriteLine(appName + " [src path] [dest path]");

                Environment.Exit(1);
            }

            var srcDir = args[0];  // source path for the location of the JSON Schema files
            var destDir = args[1]; // destination path for storing the generated source files

            // Loop through all JSON Schema files in YAML format
            foreach (var filename in Directory.GetFiles(srcDir))
            {
                // Skip files that does not have the .yaml file extension
                if (Path.GetExtension(filename).ToLower() != ".yaml")
                    continue;

                // Write information about which schema file is being parsed
                Console.WriteLine("Parsing file: " + filename);

                // Get the class name from the filename
                var className = ToClassName(filename);

                // Read the JSON Schema from the file
                var schema = JsonSchemaYaml.FromFileAsync(filename).Result;

                // Disble allowAdditionalProperties attribute as it is not provided in the schemas (currently)
                schema.AllowAdditionalProperties = false;

                // Prepare a C# code generator with our settings
                var settings = new CSharpGeneratorSettings();
                var typeResolver = new CustomizedCSharpTypeResolver(settings);

                var generator = new CSharpGenerator(schema, settings, typeResolver);
                settings.Namespace = "Robocode.TankRoyale.Schema";

                // Generate and output source file
                var text = generator.GenerateFile();

                // If no class is declared in the output file, then insert the missing class
                if (!text.Contains("public partial class"))
                {
                    // Note the class name is "Yaml" here, which is replaced later with this program
                    text = text.Insert(text.LastIndexOf('}') - 1, "public partial class Yaml\n    {\n    }\n");
                }

                // Remove embedded classes in the generated source file.
                // All files are put in individual schema files instead of all in a single big file.
                int firstClassStartIndex = text.IndexOf("\n    /// <summary>");
                int lastClassStartIndex = text.LastIndexOf("\n    /// <summary>");
                if (firstClassStartIndex > 0 && lastClassStartIndex > firstClassStartIndex)
                {
                    var count = lastClassStartIndex - firstClassStartIndex;
                    text = text.Remove(firstClassStartIndex + 1, count);
                }

                // Prepare replacement string for providing the correct class name (instead of "Yaml")
                string replacement = "public class " + className;

                // Add extension class to the class replacement string, of the schema contains an "extends" attribute
                try
                {
                    var extends = (IDictionary<string, object>) schema.ExtensionData["extends"];
                    string referencePath = (string) extends["__referencePath"];
                    replacement += " : " + ToClassName(referencePath);
                } catch (KeyNotFoundException) {}

                // Replace the "public partial class Yaml" with our replacement string
                // (correct class name + extension, if is was missing)
                text = text.Replace("public partial class Yaml", replacement);

                // Replace the "YamlType" with <class mame>Type
                string classNameType = className + "Type";
                text = text.Replace("public enum YamlType", "public enum " + classNameType)
                    .Replace("public YamlType Type", "public " + classNameType + " Type");

                // Replace MessageType enums with a string using for serializing the type
                text = text.Replace("public MessageType Type", "public string Type");

                // Write the source file to the destination folder
                var outputFilename = destDir + '/' + className + ".cs";

                File.WriteAllText(outputFilename, text.Replace("\n", "\r\n"));
            }
        }

        /// <summary>
        /// Method using for converting a file name for a schema file into a C# class name.
        /// E.g. converts the file name D:\..\robocode-tankroyale-schema\bot-death-event.yaml into BotDeathEvent
        /// </summary>
        /// <param name="filename">Is the filename of the JSON Schema to convert into a C# class name</param>
        /// <returns>A string containing a C# class name</returns>
        private static string ToClassName(string filename)
        {
            // Split the name based on dashes
            var strList = Path.GetFileNameWithoutExtension(filename).Split('-');

            // The starting letter and letters after a dash must be converted to upper case, and the dashes must be removed
            StringBuilder sb = new StringBuilder();
            foreach (string str in strList)
            {
                sb.Append(str.First().ToString().ToUpper());
                sb.Append(str.Substring(1).ToLower());
            }
            return sb.ToString();
        }

        /// <summary>
        /// This is customized CSharpTypeResolver, where the Resolve() method has been overridden in order to support
        /// nullable primitives.
        /// </summary>
        private class CustomizedCSharpTypeResolver : CSharpTypeResolver
        {
            public CustomizedCSharpTypeResolver(CSharpGeneratorSettings settings) : base(settings) {}

            public override string Resolve(JsonSchema schema, bool isNullable, string typeNameHint)
            {
                if (!isNullable && schema is JsonSchemaProperty property)
                {
                    isNullable = !property.IsRequired;
                }
                return Resolve(schema, isNullable, typeNameHint, true);
            }
        }
    }
}
