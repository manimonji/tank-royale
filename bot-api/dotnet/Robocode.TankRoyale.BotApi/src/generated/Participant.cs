//----------------------
// <auto-generated>
//     Generated using the NJsonSchema v10.1.2.0 (Newtonsoft.Json v9.0.0.0) (http://NJsonSchema.org)
// </auto-generated>
//----------------------

namespace Robocode.TankRoyale.Schema
{
    #pragma warning disable // Disable all warnings

    /// <summary>Bot participating in a battle</summary>
    [System.CodeDom.Compiler.GeneratedCode("NJsonSchema", "10.1.2.0 (Newtonsoft.Json v9.0.0.0)")]
    public class Participant 
    {
        /// <summary>Identifier for the participant in a battle</summary>
        [Newtonsoft.Json.JsonProperty("id", Required = Newtonsoft.Json.Required.DisallowNull, NullValueHandling = Newtonsoft.Json.NullValueHandling.Ignore)]
        public int? Id { get; set; }
    
        /// <summary>Name of bot, e.g. Killer Bee</summary>
        [Newtonsoft.Json.JsonProperty("name", Required = Newtonsoft.Json.Required.Always)]
        [System.ComponentModel.DataAnnotations.Required(AllowEmptyStrings = true)]
        public string Name { get; set; }
    
        /// <summary>Bot version, e.g. 1.0</summary>
        [Newtonsoft.Json.JsonProperty("version", Required = Newtonsoft.Json.Required.Always)]
        [System.ComponentModel.DataAnnotations.Required(AllowEmptyStrings = true)]
        public string Version { get; set; }
    
        /// <summary>Name of authors, e.g. John Doe (john_doe@somewhere.net)</summary>
        [Newtonsoft.Json.JsonProperty("authors", Required = Newtonsoft.Json.Required.DisallowNull, NullValueHandling = Newtonsoft.Json.NullValueHandling.Ignore)]
        public System.Collections.Generic.ICollection<string> Authors { get; set; }
    
        [Newtonsoft.Json.JsonProperty("description", Required = Newtonsoft.Json.Required.DisallowNull, NullValueHandling = Newtonsoft.Json.NullValueHandling.Ignore)]
        public string Description { get; set; }
    
        /// <summary>URL to a home page for the bot</summary>
        [Newtonsoft.Json.JsonProperty("homepage", Required = Newtonsoft.Json.Required.DisallowNull, NullValueHandling = Newtonsoft.Json.NullValueHandling.Ignore)]
        public string Homepage { get; set; }
    
        /// <summary>2-letter country code(s) defined by ISO 3166-1, e.g. "UK"</summary>
        [Newtonsoft.Json.JsonProperty("countryCodes", Required = Newtonsoft.Json.Required.DisallowNull, NullValueHandling = Newtonsoft.Json.NullValueHandling.Ignore)]
        public System.Collections.Generic.ICollection<string> CountryCodes { get; set; }
    
        /// <summary>Game types supported by this bot (defined elsewhere), e.g. "classic", "melee" and "1v1"</summary>
        [Newtonsoft.Json.JsonProperty("gameTypes", Required = Newtonsoft.Json.Required.Always)]
        [System.ComponentModel.DataAnnotations.Required]
        public System.Collections.Generic.ICollection<string> GameTypes { get; set; } = new System.Collections.ObjectModel.Collection<string>();
    
        /// <summary>Platform used for running the bot, e.g. Java 17 or .NET 5</summary>
        [Newtonsoft.Json.JsonProperty("platform", Required = Newtonsoft.Json.Required.DisallowNull, NullValueHandling = Newtonsoft.Json.NullValueHandling.Ignore)]
        public string Platform { get; set; }
    
        /// <summary>Language used for programming the bot, e.g. Java 16 or C# 9.0</summary>
        [Newtonsoft.Json.JsonProperty("programmingLang", Required = Newtonsoft.Json.Required.DisallowNull, NullValueHandling = Newtonsoft.Json.NullValueHandling.Ignore)]
        public string ProgrammingLang { get; set; }
    
    
    }
}