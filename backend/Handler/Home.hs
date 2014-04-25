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
getHomeR = defaultLayout $ do
        setTitle "Welcome To Yesod!"

postHomeR :: Handler Html
postHomeR = getHomeR

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
postImagesR latT longT = do
    let lat = read latT :: Double
    let long = read longT :: Double
    ((result, _), _) <- runFormPost uploadForm
    case result of
        FormSuccess (file, date) -> do
            newId <- runDB $ insert (Image lat long date)
            writeToServer newId file
            returnJson $ object [ "result" .= ("ok" :: Text) ]
        _ -> do
            returnJson $ object [ "result" .= ("error" :: Text) ]

uploadDirectory :: FilePath
uploadDirectory = "static"

writeToServer :: Key Image -> FileInfo -> Handler ()
writeToServer tId file = liftIO $ fileMove file (uploadDirectory </> ((show tId) ++ ".png"))

uploadForm :: Html -> MForm Handler (FormResult (FileInfo, Int), Widget)
uploadForm = renderDivs $ (,)
    <$> fileAFormReq "Image file"
    <*> lift (liftIO getTime)


getTime :: (Integral a) => IO a
getTime = getCurrentTime >>= return . floor . utctDayTime
