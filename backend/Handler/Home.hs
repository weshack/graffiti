{-# LANGUAGE TupleSections, OverloadedStrings #-}
{-# LANGUAGE FlexibleInstances   #-}
module Handler.Home where

import Import
import Data.List (sortBy)
import System.FilePath
import Data.Time (UTCTime,getCurrentTime,utctDayTime)

instance ToJSON (Entity Image) where
    toJSON (Entity tid (Image lat long time)) = object
        [ "id" .= tid, "latitude" .= lat, "longitude" .= long, "timestamp" .= time ]


getHomeR :: Handler Html
getHomeR = do
    sendFile "text/html" "static/index.html"

postHomeR :: Handler Html
postHomeR = getHomeR

getMapR :: Handler Html
getMapR = do
    sendFile "text/html" "static/map.html"

dist :: Double -> Double -> Entity Image -> Double
dist x y (Entity _ (Image lat long _)) = (x - lat) * (x - lat) + (y - long) * (y - long)

getImagesNearR :: String -> String -> Handler Value
getImagesNearR latT longT = do
    let lat = read latT :: Double
    let long = read longT :: Double
    images <- runDB $ selectList [] [] :: Handler [Entity Image]
    let closest = take 50 $ sortBy (\e1 e2 -> compare (dist lat long e1) (dist lat long e2) ) images
    returnJson closest

postUploadImagesR :: String -> String -> Handler Value
postUploadImagesR latT longT = do
    let lat = read latT :: Double
    let long = read longT :: Double
    (file,_) <- runInputPost $ (,) <$> ireq fileField "image" <*> (pure 2)
    date <- liftIO getTime
    newId <- runDB $ insert (Image lat long (fromInteger date))
    writeToServer newId file
    returnJson $ object [ "result" .= ("ok" :: Text) ]

uploadDirectory :: FilePath
uploadDirectory = "static/graffiti"

writeToServer :: Key Image -> FileInfo -> Handler ()
writeToServer tId file = case (unKey tId) of
    PersistInt64 name -> liftIO $ fileMove file (uploadDirectory </> (show name) ++ ".jpg")

getTime :: (Integral a) => IO a
getTime = getCurrentTime >>= return . floor . utctDayTime
