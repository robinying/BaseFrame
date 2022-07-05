package com.robin.baseframe.delegate

class DelegateGamePlayer(private val player: IGamePlayer):IGamePlayer by player {
}