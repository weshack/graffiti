{-# LANGUAGE TupleSections, OverloadedStrings #-}
{-# LANGUAGE FlexibleInstances   #-}
module Handler.Home where

import Import
import Data.List
import System.FilePath
import Data.Time (UTCTime,getCurrentTime)
-- This is a handler function for the GET request method on the HomeR
-- resource pattern. All of your resource patterns are defined in
-- config/routes
--
-- The majority of the code you will write in Yesod lives in these handler
-- functions. You can spread them across multiple files if you are so
-- inclined, or create a single monolithic file.

instance ToJSON (Entity Image) where
    toJSON (Entity tid (Image url lat long time)) = object
        [ "id" .= tid, "latitude" .= lat, "longitude" .= long, "timestamp" .= time ]


getHomeR :: Handler Html
getHomeR = do
    (formWidget, formEnctype) <- generateFormPost sampleForm
    let submission = Nothing :: Maybe (FileInfo, Text)
        handlerName = "getHomeR" :: Text
    defaultLayout $ do
        aDomId <- newIdent
        setTitle "Welcome To Yesod!"
        $(widgetFile "homepage")

postHomeR :: Handler Html
postHomeR = do
    ((result, formWidget), formEnctype) <- runFormPost sampleForm
    let handlerName = "postHomeR" :: Text
        submission = case result of
            FormSuccess res -> Just res
            _ -> Nothing

    defaultLayout $ do
        aDomId <- newIdent
        setTitle "Welcome To Yesod!"
        $(widgetFile "homepage")

getImagesR :: Handler Value
getImagesR = do
    images <- runDB $ selectList [] [] :: Handler [Entity Image]
    returnJson images

dist :: Double -> Double -> Entity Image -> Double
dist x y (Entity _ (Image lat long _)) = (x - lat) * (x - lat) + (y - long) * (y - long)

getImagesNearR :: String -> String -> Handler Value
getImagesNearR latT longT = do
    let lat = read latT :: Double
    let long = read longT :: Double
    images <- runDB $ selectList [] [] :: Handler [Entity Image]
    let closest = take 50 $ sortBy (\e1 e2 -> compare (dist lat long e1) (dist lat long e2) ) images
    returnJson closest

postImagesR :: String -> String -> Handler Value
postImagesR = do
    let lat = read latT :: Double
    let long = read longT :: Double
    ((result, widget), enctype) <- runFormPost uploadForm
        case result of
            FormSuccess (file, date) -> do
                -- TODO: check if image already exists
                -- save to image directory
                newId <- runDB $ insert (Image lat long date)
                filename <- writeToServer newId file
                returnJson $ object [ "result" .= "ok" ]
            _ -> do
                returnJson $ object [ "result" .= "error" ]

uploadDirectory :: FilePath
uploadDirectory = "static"

writeToServer :: Text -> FileInfo -> Handler FilePath
writeToServer tId file = do 
    liftIO $ fileMove file (uploadDirectory </> ((unpack tId) ++ ".png")
    return filename

uploadForm :: Html -> MForm App App (FormResult (FileInfo, Maybe Textarea, UTCTime), Widget)
uploadForm = renderBootstrap $ (,,)
    <$> fileAFormReq "Image file"
    <*> aformM (liftIO getCurrentTime)