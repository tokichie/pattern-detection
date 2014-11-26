open System
open System.IO
open System.Xml
open Microsoft.XmlDiffPatch

let undefined () = System.NotImplementedException () |> raise

let originalXmlPath: string = @"original.xml"
let changedXmlPath: string = @"changed.xml"
let diffgramPath: string = @"diff.xml"
let logPath: string = @"log.txt"

[<EntryPoint>]
let main argv =
    let diff = new XmlDiff()
    use diffgramWriter = XmlWriter.Create(diffgramPath)
    diff.IgnoreChildOrder <- true
    diff.IgnoreDtd <- true
    diff.IgnoreNamespaces <- true
    diff.IgnorePrefixes <- true
    diff.IgnoreWhitespace <- true
    diff.IgnoreXmlDecl <- true
    diff.IgnorePI <- true
    diff.IgnoreComments <- true
    try
        let originalXmlReader =
            let reader = new StreamReader(originalXmlPath, Text.Encoding.GetEncoding("shift_jis"))
            let settings = new XmlReaderSettings()
            settings.DtdProcessing <- DtdProcessing.Parse
            XmlReader.Create(reader, settings)
        let changedXmlReader =
            let reader = new StreamReader(changedXmlPath, Text.Encoding.GetEncoding("shift_jis"))
            let settings = new XmlReaderSettings()
            settings.DtdProcessing <- DtdProcessing.Parse
            XmlReader.Create(reader, settings)
        diff.Compare(originalXmlReader, changedXmlReader, diffgramWriter) |> ignore
    with
    | e -> File.WriteAllText(logPath, sprintf "Exception:\n%s\n%s\n" e.Message e.StackTrace)
    0